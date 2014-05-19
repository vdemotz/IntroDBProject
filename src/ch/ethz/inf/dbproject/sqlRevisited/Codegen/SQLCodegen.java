package ch.ethz.inf.dbproject.sqlRevisited.Codegen;

import java.util.Arrays;
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
						 new GeneratorProjectAndAggregate(), new GeneratorRename(), new GeneratorSelection(arguments), new GeneratorSort());	
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
			// TODO resolve grouping list
			// TODO instantiate group operator
			return null;
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
	
	class GeneratorProjectAndAggregate implements TransformUnary<SyntaxTreeProjectAndAggregateOperatorNode, SQLOperator>
	{
		@Override
		public SQLOperator transform(SyntaxTreeProjectAndAggregateOperatorNode currentNode, SQLOperator childResult) throws SQLSemanticException {
			// TODO
			if (currentNode.schema.equals(childResult.schema)) {
				return childResult;
			}
			return null;
		}
	}
	
	class GeneratorRename implements TransformUnary<SyntaxTreeRenameTableNode, SQLOperator>
	{
		@Override
		public SQLOperator transform(SyntaxTreeRenameTableNode currentNode, SQLOperator childResult) throws SQLSemanticException {
			// TODO Copy childResult with new schema
			return null;
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
			
			if (leftConstant.getClass().equals(String.class)) {
				return new MaterializingPredicate<String>(new MaterializerConstant<String>((String)leftConstant), new MaterializerConstant<String>((String)rightConstant), operator);
			} else if (leftConstant.getClass().equals(Integer.class)) {
				return new MaterializingPredicate<Integer>(new MaterializerConstant<Integer>((Integer)leftConstant), new MaterializerConstant<Integer>((Integer)rightConstant), operator);
			} else if (leftConstant.getClass().equals(Boolean.class)) {
				return new MaterializingPredicate<Boolean>(new MaterializerConstant<Boolean>((Boolean)leftConstant), new MaterializerConstant<Boolean>((Boolean)rightConstant), operator);
			} else {
				throw new SQLSemanticException(SQLSemanticException.Type.TypeError);
			}
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
			//TODO: flip order if necessary
			int idIndex = schema.indexOf(identifier);
			SQLType type = schema.getAttributesTypes()[idIndex];
			int attributeByteOffset = schema.getSizeOfAttributes(idIndex);

			//TODO other types
			if (type.type == SQLType.BaseType.Varchar) {
				return new MaterializingPredicate<String>(new VarcharMaterializer(attributeByteOffset), new MaterializerConstant<String>((String)constant), operator);
				
			} else if (type.type == SQLType.BaseType.Char) {
				throw new SQLSemanticException(SQLSemanticException.Type.TypeError);
				
			} else if (type.type == SQLType.BaseType.Integer) {
				return new MaterializingPredicate<Integer>(new IntegerMaterializer(attributeByteOffset), new MaterializerConstant<Integer>((Integer)constant), operator);
				
			} else if (type.type == SQLType.BaseType.Date) {
				return new MaterializingPredicate<String>(new VarcharMaterializer(attributeByteOffset), new MaterializerConstant<String>((String)constant), operator);
				
			} else if (type.type == SQLType.BaseType.Datetime) {
				return new MaterializingPredicate<String>(new VarcharMaterializer(attributeByteOffset), new MaterializerConstant<String>((String)constant), operator);
				
			} else if (type.type == SQLType.BaseType.Boolean) {
				throw new SQLSemanticException(SQLSemanticException.Type.TypeError);
				
			} else {
				throw new SQLSemanticException(SQLSemanticException.Type.TypeError);
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
			// TODO resolve sort relation
			// TODO instantiate appropriate sort operator
			return null;
		}
	}
	
	/////
	///PREDICATE INSTANCIATION
	/////
	
	private Predicate<byte[]> resolveTwoSidedPredicate(TableSchema schema, Pair<String, String> leftIdentifier, Pair<String, String> rightIdentifier, PredicateFromComparison operator) throws SQLSemanticException {
		
		int leftIndex = schema.indexOf(leftIdentifier);
		int rightIndex = schema.indexOf(rightIdentifier);
		SQLType type = schema.getAttributesTypes()[leftIndex];
		
		if (!type.equals(schema.getAttributesTypes()[rightIndex])) {
			throw new SQLSemanticException(SQLSemanticException.Type.TypeError, type.toString() + " != " + schema.getAttributesTypes()[rightIndex]);
		}
		
		int leftAttributeByteOffset = schema.getSizeOfAttributes(leftIndex);
		int rightAttributeByteOffset = schema.getSizeOfAttributes(rightIndex);
		
		if (type.type == SQLType.BaseType.Varchar) {
			return new MaterializingPredicate<String>(new VarcharMaterializer(leftAttributeByteOffset), new VarcharMaterializer(rightAttributeByteOffset), operator);
			
		} else if (type.type == SQLType.BaseType.Char) {
			throw new SQLSemanticException(SQLSemanticException.Type.TypeError);
			
		} else if (type.type == SQLType.BaseType.Integer) {
			return new MaterializingPredicate<Integer>(new IntegerMaterializer(leftAttributeByteOffset), new IntegerMaterializer(rightAttributeByteOffset), operator);
			
		} else if (type.type == SQLType.BaseType.Date) {
			return new MaterializingPredicate<String>(new VarcharMaterializer(leftAttributeByteOffset), new VarcharMaterializer(rightAttributeByteOffset), operator);
			
		} else if (type.type == SQLType.BaseType.Datetime) {
			return new MaterializingPredicate<String>(new VarcharMaterializer(leftAttributeByteOffset), new VarcharMaterializer(rightAttributeByteOffset), operator);
			
		} else if (type.type == SQLType.BaseType.Boolean) {
			throw new SQLSemanticException(SQLSemanticException.Type.TypeError);
			
		} else {
			throw new SQLSemanticException(SQLSemanticException.Type.TypeError);
		}
	}
	
	
	////
	//MATERIALIZER IMPLEMENTATION
	////
	
	public class VarcharMaterializer implements Materializer<String>
	{
		
		int attributeByteOffset;
		
		VarcharMaterializer(int offsetOfAttributeInBytes) {
			attributeByteOffset = offsetOfAttributeInBytes;
		}
		
		@Override
		public String get(byte[] bytes) {
			//TODO (avoid copying all the time)
			return Serializer.getStringFromByteArray(Arrays.copyOfRange(bytes, attributeByteOffset, bytes.length));
			//return Serializer.getVarcharFromByteArray(bytes, attributeByteOffset);
		}
	}
	
	public class IntegerMaterializer implements Materializer<Integer>
	{
		int attributeByteOffset;
		
		IntegerMaterializer(int offsetOfAttributeInBytes) {
			attributeByteOffset = offsetOfAttributeInBytes;
		}
		
		@Override
		public Integer get(byte[] bytes) {
			//TODO (avoid copying all the time)
			return Serializer.getIntegerFromByteArray(Arrays.copyOfRange(bytes, attributeByteOffset, bytes.length));
			//return Serializer.getVarcharFromByteArray(bytes, attributeByteOffset);
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
