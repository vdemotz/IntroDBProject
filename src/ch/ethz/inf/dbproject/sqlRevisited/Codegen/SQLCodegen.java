package ch.ethz.inf.dbproject.sqlRevisited.Codegen;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.ethz.inf.dbproject.Pair;
import ch.ethz.inf.dbproject.sqlRevisited.PhysicalTableInterface;
import ch.ethz.inf.dbproject.sqlRevisited.SQLType;
import ch.ethz.inf.dbproject.sqlRevisited.Serializer;
import ch.ethz.inf.dbproject.sqlRevisited.TableConnection;
import ch.ethz.inf.dbproject.sqlRevisited.TableSchema;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.*;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.SyntaxTreeNode.*;
import ch.ethz.inf.dbproject.sqlRevisited.SQLType.BaseType;

public class SQLCodegen {

	public SQLOperator generateSelectStatement(SyntaxTreeNode node, List<PhysicalTableInterface> tables, Object [] arguments) throws SQLSemanticException {
		return node.fold(new GeneratorBase(tables), new GeneratorCross(), new GeneratorJoin(arguments), new GeneratorGroup(), new GeneratorDistinct(),
						 new GeneratorProject(), new GeneratorRename(), new GeneratorSelection(arguments), new GeneratorSort(), null);	
	}
	
	////
	//CODE GENERATION :: IMPLEMENTATION
	////
	
	class GeneratorBase implements TransformBase<SyntaxTreeBaseRelationNode, SQLOperator>
	{
		private final Map<String, PhysicalTableInterface> baseTables;
		
		GeneratorBase(List<PhysicalTableInterface> tables) {
			baseTables = new HashMap<String, PhysicalTableInterface>();
			for (PhysicalTableInterface table : tables) {
				baseTables.put(table.getTableSchema().tableName, table);
			}
		}
		
		@Override
		public SQLOperator transform(SyntaxTreeBaseRelationNode currentNode) throws SQLSemanticException {
			// get TableConnection from Database
			PhysicalTableInterface table = baseTables.get(currentNode.name);
			if (table == null) {
				throw new SQLSemanticException(SQLSemanticException.Type.NoSuchTableException, currentNode.name);
			}
			// instantiate SQLOperatorBase with TableConnection and schema
			return new SQLOperatorBase(currentNode.schema, table);
		}
		
	}
	
	class GeneratorCross implements TransformBinary<SyntaxTreeCrossNode, SQLOperator>
	{
		@Override
		public SQLOperator transform(SyntaxTreeCrossNode currentNode, SQLOperator leftChildResult, SQLOperator rightChildResult) throws SQLSemanticException {
			return new SQLOperatorCross(currentNode.schema, leftChildResult, rightChildResult);
		}
	}
	
	class GeneratorJoin implements TransformBinary<SyntaxTreeJoinNode, SQLOperator>
	{
		private final Object[] arguments;
		private final PredicateFromComparison equalComparator = new PredicateFromComparison(false, true, false);
		
		public GeneratorJoin(Object[] arguments) {
			this.arguments = arguments;
		}

		@Override
		public SQLOperator transform(SyntaxTreeJoinNode currentNode, SQLOperator leftChildResult, SQLOperator rightChildResult) throws SQLSemanticException {
			// Resolve predicate
			Predicate<byte[]> predicate = resolveTwoSidedPredicate(currentNode.schema, currentNode.leftIdentifier, currentNode.rightIdentifier, equalComparator);
			
			// instantiate nested loops join operator by reducing to cross product & selection
			SQLOperatorCross cross = new SQLOperatorCross(currentNode.schema, leftChildResult, rightChildResult);
			return new SQLOperatorSelectionScan(currentNode.schema, cross, predicate);
			
			//TODO use index join when applicable
		}
	}
	
	class GeneratorGroup implements TransformUnary<SyntaxTreeGroupByNode, SQLOperator>
	{
		@Override
		public SQLOperator transform(SyntaxTreeGroupByNode currentNode, SQLOperator childResult) throws SQLSemanticException {
			// resolve grouping list
			Comparator<byte[]> comparator = resolveGroupByList(currentNode.schema, currentNode.groupByList());
			// instantiate group operator (here merge sort group)
			return new SQLOperatorSortGroup(currentNode.schema, childResult, comparator);
		}

		private Comparator<byte[]> resolveGroupByList(TableSchema schema, SyntaxTreeListNode<SyntaxTreeIdentifierNode> groupByList) throws SQLSemanticException {
			Pair<String, String> identifier = groupByList.getNode().generatingToken.getFragmentsForIdentifier();
			Materializer materializer = materializerForAttribute(schema, identifier);
			
			Comparator<byte[]> comparator = new NaturalComparatorFromMaterializer(materializer, true);
			
			if (groupByList.getNext() == null) {
				return comparator;
			} else {
				return new CompositeLexicographicalComparator<byte[]>(comparator, resolveGroupByList(schema, groupByList.getNext()));
			}
		}
	}
	
	class GeneratorDistinct implements TransformUnary<SyntaxTreeNodeDistinct, SQLOperator>
	{
		@Override
		public SQLOperator transform(SyntaxTreeNodeDistinct currentNode, SQLOperator childResult) throws SQLSemanticException {
			// TODO Reduce to grouping, projecting onto self
			return null;
		}
	}
	
	class GeneratorProject implements TransformUnary<SyntaxTreeProjectOperatorNode, SQLOperator>
	{
		@Override
		public SQLOperator transform(SyntaxTreeProjectOperatorNode currentNode, SQLOperator childResult) throws SQLSemanticException {
			if (SQLOperatorGrouping.class.isAssignableFrom(childResult.getClass())) {
				childResult = new SQLOperatorGroupReduction(childResult.schema, childResult);
			}
			return new SQLOperatorProjection(currentNode.schema, childResult);
		}
	}
	
	class GeneratorRename implements TransformUnary<SyntaxTreeRenameNode, SQLOperator>
	{
		@Override
		public SQLOperator transform(SyntaxTreeRenameNode currentNode, SQLOperator childResult) throws SQLSemanticException {
			return childResult.copyWithSchema(currentNode.schema);
		}
	}
	
	class GeneratorSelection implements TransformUnary<SyntaxTreeSelectionOperatorNode, SQLOperator>
	{
		private final Object[] arguments;
		
		public GeneratorSelection(Object[] arguments) {
			this.arguments = arguments;
		}

		@Override
		public SQLOperator transform(SyntaxTreeSelectionOperatorNode currentNode, SQLOperator childResult) throws SQLSemanticException {
			
			// Resolve predicate
			Predicate<byte[]> predicate;
			
			PredicateFromComparison operator = resolveOperatorToken(currentNode.getOperator().generatingToken);
			
			if (currentNode.leftIdentifier != null && currentNode.rightIdentifier != null) {
				predicate = resolveTwoSidedPredicate(currentNode.schema, currentNode.leftIdentifier, currentNode.rightIdentifier, operator);
				
			} else if (currentNode.leftIdentifier == null && currentNode.rightIdentifier != null) {
				predicate = resolveOneSidedPredicate(currentNode.schema, currentNode.rightIdentifier, currentNode.getLeftValue().generatingToken, operator, true);
				
			} else if (currentNode.leftIdentifier != null && currentNode.rightIdentifier == null) {
				predicate = resolveOneSidedPredicate(currentNode.schema, currentNode.leftIdentifier, currentNode.getRightValue().generatingToken, operator, false);
				
			} else {
				predicate = resolveZeroSidedPredicate(currentNode.getLeftValue().generatingToken, currentNode.getRightValue().generatingToken, operator);
				
			}
			//Generate appropriate selection operator
			return new SQLOperatorSelectionScan(currentNode.schema, childResult, predicate);
			
			//TODO generator index-select if possible
		}
		
		private Predicate<byte[]> resolveZeroSidedPredicate(SQLToken leftValue, SQLToken rightValue, PredicateFromComparison operator) throws SQLSemanticException {
			Object leftConstant = resolveConstant(leftValue);
			Object rightConstant = resolveConstant(rightValue);
			
			if (! leftConstant.getClass().equals(rightConstant.getClass())) {
				throw new SQLSemanticException(SQLSemanticException.Type.TypeError);
			}
			
			return new MaterializingPredicate(new MaterializerConstant(leftConstant), new MaterializerConstant(rightConstant), operator);
		}

		/**
		 * compares [identifer] [operatorToken] [constantToken] if flipOperator is false
		 * and  	[constantToken] [operatorToken] [identifier] otherwise
		 * Here, [x] denotes the semantic meaning of the syntactic element x.
		 *  
		 * @param schema
		 * @param identifier
		 * @param constantToken
		 * @param operatorToken
		 * @param flipOperator
		 * @return
		 * @throws SQLSemanticException
		 */
		private Predicate<byte[]> resolveOneSidedPredicate(TableSchema schema, Pair<String, String> identifier, SQLToken constantToken, PredicateFromComparison operator, boolean flipOperator) throws SQLSemanticException {
			Object constant = resolveConstant(constantToken);
			if (flipOperator) {
				return new MaterializingPredicate(new MaterializerConstant(constant), materializerForAttribute(schema, identifier), operator);
			} else {
				return new MaterializingPredicate(materializerForAttribute(schema, identifier), new MaterializerConstant(constant), operator);
			}
		}
		
		private Object resolveConstant(SQLToken token) throws SQLSemanticException {
			if (token.tokenClass == SQLToken.SQLTokenClass.ARGUMENT) {
				if (token.identifier >= arguments.length) {
					throw new SQLSemanticException(SQLSemanticException.Type.NotEnoughArgumentsProvided);
				}
				return arguments[token.identifier];
			} else if (token.tokenClass == SQLToken.SQLTokenClass.LITERAL) {
				return token.content;
			} else if (token.tokenClass == SQLToken.SQLTokenClass.NUMERIC) {
				return token.identifier;
			} else if (token.tokenClass == SQLToken.SQLTokenClass.BOOL) {
				if (token.content.equals("true")) {
					return true;
				} else {
					return false;
				}
			} else {
				throw new SQLSemanticException(SQLSemanticException.Type.InternalError);
			}
		}
		
		private PredicateFromComparison resolveOperatorToken(SQLToken operatorToken) {
			//TODO : handle like
			String cur = operatorToken.content;
			boolean allowsLess = false;
			boolean allowsGreater = false;
			boolean allowsEqual = false;
			if (cur.charAt(0) == '<') {
				allowsLess = true;
				cur = cur.substring(1);
			} else if (cur.charAt(0) == '>') {
				allowsGreater = true;
				cur = cur.substring(1);
			}
			if (cur.length() > 0 && cur.charAt(0) == '=') {
				allowsEqual = true;
			}
			return new PredicateFromComparison(allowsLess, allowsEqual, allowsGreater);
		}

	}
	
	class GeneratorSort implements TransformUnary<SyntaxTreeSortOperatorNode, SQLOperator>
	{
		@Override
		public SQLOperator transform(SyntaxTreeSortOperatorNode currentNode, SQLOperator childResult) throws SQLSemanticException {
			// resolve sort relation
			Comparator<byte[]> comparator = resolveComparator(currentNode.schema, currentNode.getOrderStatement());

			// instantiate appropriate sort operator
			return new SQLOperatorMergeSort(currentNode.schema, childResult, comparator);
		}

		private Comparator<byte[]> resolveComparator(TableSchema schema, SyntaxTreeListNode<SyntaxTreeOrderingNode> orderStatement) throws SQLSemanticException {

			Pair<String, String> identifier = orderStatement.getNode().identifier;
			Materializer materializer = materializerForAttribute(schema, identifier);
			
			Comparator<byte[]> comparator = new NaturalComparatorFromMaterializer(materializer, orderStatement.getNode().ascending);
			
			if (orderStatement.getNext() == null) {
				return comparator;
			} else {
				return new CompositeLexicographicalComparator<byte[]>(comparator, resolveComparator(schema, orderStatement.getNext()));
			}
		}
		
	}
	
	////
	//COMPARATOR IMPLEMENTATION
	////
	
	class NaturalComparatorFromMaterializer implements Comparator<byte[]>
	{
		//Given a materializer of a comparable type, returns a comparator that uses compareTo
		//if ascending is false, the order is reversed
		
		private final Materializer materializer;
		private final boolean ascending;
		
		public NaturalComparatorFromMaterializer(Materializer materializer, boolean ascending) {
			this.materializer = materializer;
			this.ascending = ascending;
		}

		@Override
		public int compare(byte[] o1, byte[] o2) {
			int comparison = ((Comparable) materializer.get(o1)).compareTo(materializer.get(o2));
			return ascending ? comparison : -comparison;
		}
		
	}
	
	class  CompositeLexicographicalComparator<T> implements Comparator<T>
	{
		private final Comparator<T> current;
		private final Comparator<T> next;
		
		public CompositeLexicographicalComparator(Comparator<T> current, Comparator<T> next) {
			this.current = current;
			this.next = next;
		}

		/**
		 * If the current comparator returns a nonzero result, it is returned.
		 * Otherwise the result of the next comparator is returned.
		 * @param current
		 * @param next
		 */
		@Override
		public int compare(T o1, T o2) {
			int comparison = current.compare(o1, o2);
			if (comparison == 0) {
				comparison = next.compare(o1, o2);
			}
			return comparison;
		}
		
	}
	
	/////
	///PREDICATE INSTANCIATION
	/////
	

	private Predicate<byte[]> resolveTwoSidedPredicate(TableSchema schema, Pair<String, String> leftIdentifier, Pair<String, String> rightIdentifier, PredicateFromComparison operator) throws SQLSemanticException {
		return new MaterializingPredicate(materializerForAttribute(schema, leftIdentifier), materializerForAttribute(schema, rightIdentifier), operator);
	}
	
	private Materializer materializerForAttribute(TableSchema schema, Pair<String, String> identifier) throws SQLSemanticException
	{
		int index = schema.indexOf(identifier);
		SQLType type = schema.getAttributesTypes()[index];
		
		int byteOffset = schema.getSizeOfAttributes(index);

		if (type.type == SQLType.BaseType.Varchar) {
			return new VarcharMaterializer(byteOffset, type.byteSizeOfType());
			
		} else if (type.type == SQLType.BaseType.Char) {
			throw new SQLSemanticException(SQLSemanticException.Type.TypeError);//TODO
			
		} else if (type.type == SQLType.BaseType.Integer) {
			return new IntegerMaterializer(byteOffset);
			
		} else if (type.type == SQLType.BaseType.Date) {
			return new VarcharMaterializer(byteOffset, type.byteSizeOfType());
			
		} else if (type.type == SQLType.BaseType.Datetime) {
			return new VarcharMaterializer(byteOffset, type.byteSizeOfType());
			
		} else if (type.type == SQLType.BaseType.Boolean) {
			return new BooleanMaterializer(byteOffset);
			
		} else {
			throw new SQLSemanticException(SQLSemanticException.Type.TypeError);
		}
	}
	
	
	////
	//MATERIALIZER IMPLEMENTATION
	////
	
	public class VarcharMaterializer extends AttributeMaterializer<String>
	{
		VarcharMaterializer(int offsetOfAttributeInBytes, int width) {
			super(offsetOfAttributeInBytes, width);
		}
		
		@Override
		public String get(byte[] bytes) {
			//TODO (avoid copying all the time)
			return Serializer.getStringFromByteArray(Arrays.copyOfRange(bytes, attributeByteOffset, attributeByteEndOffset));
		}
	}
	
	public class IntegerMaterializer extends AttributeMaterializer<Integer>
	{
		IntegerMaterializer(int offsetOfAttributeInBytes) {
			super(offsetOfAttributeInBytes, SQLType.INT_BYTE_SIZE);
		}
		
		@Override
		public Integer get(byte[] bytes) {
			//TODO (avoid copying all the time)
			return Serializer.getIntegerFromByteArray(Arrays.copyOfRange(bytes, attributeByteOffset, attributeByteEndOffset));
		}
	}
	
	public class BooleanMaterializer  extends AttributeMaterializer<Boolean>
	{

		BooleanMaterializer(int offsetOfAttributeInBytes) {
			super(offsetOfAttributeInBytes, SQLType.CHARACTER_BYTE_SIZE);
		}

		@Override
		public Boolean get(byte[] bytes) {
			return Serializer.getBooleanFromByteArray(Arrays.copyOfRange(bytes, attributeByteOffset, attributeByteEndOffset));
		}
		
	}
	
	public abstract class AttributeMaterializer<T> implements Materializer<T>
	{
		int attributeByteOffset;
		int attributeByteEndOffset;
		AttributeMaterializer(int offsetOfAttributeInBytes, int width) {
			attributeByteOffset = offsetOfAttributeInBytes;
			attributeByteEndOffset = attributeByteOffset+width;
		}
	}
	
	public class MaterializerConstant<T> implements Materializer<T> {

		private final T constant;
		
		MaterializerConstant(T constant) {
			this.constant = constant;
		}
		
		@Override
		public T get(byte[] bytes) {
			return constant;
		}
	}
}
