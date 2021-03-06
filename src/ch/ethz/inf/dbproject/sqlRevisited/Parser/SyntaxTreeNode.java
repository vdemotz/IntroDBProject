package ch.ethz.inf.dbproject.sqlRevisited.Parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


import ch.ethz.inf.dbproject.Pair;
import ch.ethz.inf.dbproject.sqlRevisited.SQLType;
import ch.ethz.inf.dbproject.sqlRevisited.TableSchema;
import ch.ethz.inf.dbproject.sqlRevisited.TableSchemaAttributeDetail;

public class SyntaxTreeNode {

	public final TableSchema schema;//may be null, but if it isn't then all descendants have a non null schema
	protected final ImmutableArray<SyntaxTreeNode> children;//never null
	
	SyntaxTreeNode(TableSchema schema, SyntaxTreeNode...children) {
		this.children = new ImmutableArray<SyntaxTreeNode>(children);
		this.schema = schema;
	}
	
	SyntaxTreeNode(SyntaxTreeNode...children) {
		this.children =  new ImmutableArray<SyntaxTreeNode>(children);
		this.schema = null;
	}
	
	/**
	 * Infers the schema for this node and all its operator descendants
	 * the schema includes the names, qualifiers and types of the result output by a particular operator
	 * @param schemata
	 * @return an augmented, syntactically equivalent syntax tree
	 * @throws SQLSemanticException if a BaseRelationNode references a table that is not in the list of schemata,
	 * 								or if a SyntaxTreeProjectAndAggregateOperator refers to an attribute thats not in its child schema
	 * 								or if an unexpected node structure is encountered
	 */
	public SyntaxTreeNode instanciateWithSchemata(List<TableSchema> schemata) throws SQLSemanticException {
		return fold(new InstanciateSchemaBase(schemata), new InstanciateSchemaCross(), new InstanciateSchemaJoin(), new InstanciateSchemaGroup(), new InstanciateSchemaDistinct(),
					new InstanciateSchemaProject(), new InstanciateSchemaRename(), new InstanciateSchemaSelection(), new InstanciateSchemaSort(), new InstanciateSchemaAggregate());
	}
	
	/**
	 * Rewrites the syntax tree to a semantically equivalent tree that can perform queries faster
	 * This involves pushing down selection as far as possible and introducing joins where possible
	 * @return a semantically equivalent syntax tree
	 * @throws SQLSemanticException
	 */
	public SyntaxTreeNode rewrite() throws SQLSemanticException
	{
		return fold(new RewriteBase(), new RewriteCross(), new RewriteJoin(), new RewriteGroup(), new RewriteDistinct(),
					new RewriteProject(), new RewriteRename(), new RewriteSelection(), new RewriteSort(), new RewriteAggregate());
	}
	
	/**
	 * Returns a map containing the types of the arguments of a query.
	 * Currently supports arguments of the form id (op) ? or ? (op) id
	 * @return a map containing the types of the arguments of this query.
	 * @throws SQLSemanticException other types of arguments, in particular comparisons of the form ?=? or comparisons with literals raise an exception
	 */
	public Map<Integer, SQLType> inferArgumentTypes() throws SQLSemanticException {
		Map<Integer, SQLType> inferredArguments = fold(new NullTransform<SyntaxTreeBaseRelationNode, Map<Integer, SQLType>>(),
														new AppendMapsTransform<SyntaxTreeCrossNode, Integer, SQLType>(),
														new AppendMapsTransform<SyntaxTreeJoinNode, Integer, SQLType>(),
														new IdentityTransform<SyntaxTreeGroupByNode ,Map<Integer, SQLType>>(),
														new IdentityTransform<SyntaxTreeNodeDistinct, Map<Integer,SQLType>>(),
														new IdentityTransform<SyntaxTreeProjectOperatorNode, Map<Integer, SQLType>>(),
														new IdentityTransform<SyntaxTreeRenameNode, Map<Integer, SQLType>>(),
														new SelectionArgumentTypeInference(),
														new IdentityTransform<SyntaxTreeSortOperatorNode, Map<Integer,SQLType>>(),
														new IdentityTransform<SyntaxTreeAggregateNode, Map<Integer,SQLType>>()
														);
		
		return appendMaps(new TreeMap<Integer, SQLType>(), inferredArguments); //Make sure result is non-null by appending to empty map
	}
	
	////
	//REWRITE IMPLEMENTATION
	////
	
	private class RewriteSort implements TransformUnary<SyntaxTreeSortOperatorNode, SyntaxTreeNode> {
		@Override
		public SyntaxTreeNode transform(SyntaxTreeSortOperatorNode currentNode, SyntaxTreeNode childResult) throws SQLSemanticException {
			return new SyntaxTreeSortOperatorNode(currentNode.schema, childResult, currentNode.getOrderStatement());
		}
	}
	
	private class RewriteSelection implements TransformUnary<SyntaxTreeSelectionOperatorNode, SyntaxTreeNode> {
		@Override
		public SyntaxTreeNode transform(SyntaxTreeSelectionOperatorNode currentNode, SyntaxTreeNode childResult) throws SQLSemanticException {
			
			SyntaxTreeSelectionOperatorNode oldRoot = currentNode.copyWithChild(childResult);
			SyntaxTreeNode newRoot = pushDown(oldRoot);//push down selection as far as possible
			return newRoot;
		}
		
		public SyntaxTreeNode pushDown(SyntaxTreeSelectionOperatorNode node) throws SQLSemanticException {
			if (node.getChild().getClass().equals(SyntaxTreeSelectionOperatorNode.class)) {//Case selection : always push down
				SyntaxTreeSelectionOperatorNode child = (SyntaxTreeSelectionOperatorNode)node.getChild();
				SyntaxTreeSelectionOperatorNode nodePointingToGrandchild = node.copyWithChild(child.getChild());
				return child.copyWithChild(pushDown(nodePointingToGrandchild));//recursively push down
				
			} else if(node.getChild().getClass().equals(SyntaxTreeCrossNode.class) || node.getChild().getClass().equals(SyntaxTreeJoinNode.class)) {//Case cross or join
				SyntaxTreeBinaryNode child = (SyntaxTreeBinaryNode)node.getChild();
				
				boolean leftChildHasLeftAttribute = child.getLeft().schema.hasAttribute(node.leftIdentifier);
				boolean leftChildHasRightAttribute = child.getLeft().schema.hasAttribute(node.rightIdentifier);
				boolean rightChildHasRightAttribute = child.getRight().schema.hasAttribute(node.rightIdentifier);
				boolean rightChildHasLeftAttribute = child.getRight().schema.hasAttribute(node.leftIdentifier);
				
				if (!rightChildHasRightAttribute && !rightChildHasLeftAttribute) {//Sub-Case Push Left
					SyntaxTreeSelectionOperatorNode nodePointingToLeftGrandchild = node.copyWithChild(child.getLeft());
					return child.copyWithLeftChild(pushDown(nodePointingToLeftGrandchild));//recursively push down the left subtree and reassemble
					
				} else if (!leftChildHasRightAttribute && !leftChildHasLeftAttribute) {//Sub-Case Push Right
					SyntaxTreeSelectionOperatorNode nodePointingToRightGrandchild = node.copyWithChild(child.getRight());
					return child.copyWithRightChild(pushDown(nodePointingToRightGrandchild));//recursively push down the right subtree and reassemble
						
				} else if (node.getChild().getClass().equals(SyntaxTreeCrossNode.class) && node.getOperator().generatingToken.tokenClass == SQLToken.SQLTokenClass.EQUAL) {//Sub-case create Join
					if (leftChildHasRightAttribute && rightChildHasLeftAttribute) {
						return new SyntaxTreeJoinNode(node.schema, child.getLeft(), child.getRight(), node.rightIdentifier, node.leftIdentifier);
						
					} else if (leftChildHasLeftAttribute && rightChildHasRightAttribute) {
						return new SyntaxTreeJoinNode(node.schema, child.getLeft(), child.getRight(), node.leftIdentifier, node.rightIdentifier);
						
					} else {
						throw new SQLSemanticException(SQLSemanticException.Type.InternalError);
					}
					
				} else {//Sub-Case No Push : don't push
					return node;
				}
				
			} else {//Case else : don't push
				return node;
			}
		}
	}
	
	private class RewriteJoin implements TransformBinary<SyntaxTreeJoinNode, SyntaxTreeNode> {
		@Override
		public SyntaxTreeNode transform(SyntaxTreeJoinNode currentNode, SyntaxTreeNode leftChildResult, SyntaxTreeNode rightChildResult) throws SQLSemanticException {
			return currentNode.copyWithChildren(leftChildResult, rightChildResult);
		}
	}
	
	private class RewriteRename implements TransformUnary<SyntaxTreeRenameNode, SyntaxTreeNode> {
		@Override
		public SyntaxTreeNode transform(SyntaxTreeRenameNode currentNode, SyntaxTreeNode childResult) throws SQLSemanticException {
			return new SyntaxTreeRenameNode(currentNode.schema, childResult);
		}
	}
	
	private class RewriteProject implements TransformUnary<SyntaxTreeProjectOperatorNode, SyntaxTreeNode> {
		@Override
		public SyntaxTreeNode transform(SyntaxTreeProjectOperatorNode currentNode, SyntaxTreeNode childResult) throws SQLSemanticException {
			return new SyntaxTreeProjectOperatorNode(currentNode.schema, childResult, currentNode.getProjectionList());
		}
	}
	
	private class RewriteDistinct implements TransformUnary<SyntaxTreeNodeDistinct, SyntaxTreeNode> {
		@Override
		public SyntaxTreeNode transform(SyntaxTreeNodeDistinct currentNode, SyntaxTreeNode childResult) throws SQLSemanticException {
			return new SyntaxTreeNodeDistinct(currentNode.schema, childResult);
		}
	}
	
	private class RewriteGroup implements TransformUnary<SyntaxTreeGroupByNode, SyntaxTreeNode> {
		@Override
		public SyntaxTreeNode transform(SyntaxTreeGroupByNode currentNode, SyntaxTreeNode childResult) throws SQLSemanticException {
			return new SyntaxTreeGroupByNode(currentNode.schema, childResult, currentNode.groupByList());
		}
	}
	
	private class RewriteCross implements TransformBinary<SyntaxTreeCrossNode, SyntaxTreeNode> {
		@Override
		public SyntaxTreeNode transform(SyntaxTreeCrossNode currentNode, SyntaxTreeNode leftChildResult, SyntaxTreeNode rightChildResult) throws SQLSemanticException {
			 return new SyntaxTreeCrossNode(currentNode.schema, leftChildResult, rightChildResult);
		}
	}
	
	private class RewriteBase implements TransformBase<SyntaxTreeBaseRelationNode, SyntaxTreeNode> {
		@Override
		public SyntaxTreeNode transform(SyntaxTreeBaseRelationNode currentNode) throws SQLSemanticException {
			return currentNode;
		}
	}
	
	private class RewriteAggregate implements TransformUnary<SyntaxTreeAggregateNode, SyntaxTreeNode> {
		@Override
		public SyntaxTreeNode transform(SyntaxTreeAggregateNode currentNode, SyntaxTreeNode childResult) throws SQLSemanticException {
			return new SyntaxTreeAggregateNode(currentNode.schema, currentNode.generatingToken, currentNode.aggregateName, childResult);
		}
	}
	
	////
	//PSEUDO-FOLD OPERATORS
	////
	
	public interface TransformBase<S, T> {
		T transform(S currentNode) throws SQLSemanticException;
	}
	
	public interface TransformUnary<S, T> {
		T transform(S currentNode, T childResult) throws SQLSemanticException;
	}
	
	public interface TransformBinary<S, T> {
		T transform(S currentNode, T leftChildResult, T rightChildResult) throws SQLSemanticException;
	}
	
	/**
	 * Fold operation over all non-list, non-identifier nodes of the subtree rooted at this node
	 * @param base
	 * @param cross
	 * @param join
	 * @param group
	 * @param distinct
	 * @param project
	 * @param rename
	 * @param selection
	 * @param sort
	 * @return
	 * @throws SQLSemanticException
	 */
	public <T> T fold(TransformBase<SyntaxTreeBaseRelationNode, T> base,
					  TransformBinary<SyntaxTreeCrossNode, T> cross,
					  TransformBinary<SyntaxTreeJoinNode, T> join,
					  TransformUnary<SyntaxTreeGroupByNode, T> group,
					  TransformUnary<SyntaxTreeNodeDistinct, T> distinct,
					  TransformUnary<SyntaxTreeProjectOperatorNode, T> project,
					  TransformUnary<SyntaxTreeRenameNode, T> rename,
					  TransformUnary<SyntaxTreeSelectionOperatorNode, T> selection,
					  TransformUnary<SyntaxTreeSortOperatorNode, T> sort,
					  TransformUnary<SyntaxTreeAggregateNode, T> aggregate
					  ) throws SQLSemanticException
	{
		ArrayList<T> childResults = new ArrayList<T>();
		for (SyntaxTreeNode child : children) {
			if (!child.getClass().equals(SyntaxTreeListNode.class) && !child.getClass().equals(SyntaxTreeIdentifierNode.class)) {//do not fold over list nodes, identifiers
				childResults.add (child.fold(base, cross, join, group, distinct, project, rename, selection, sort, aggregate));
			}
		}
		
		T result = null;
		if (this.getClass().equals(SyntaxTreeBaseRelationNode.class)) {
			result = base.transform((SyntaxTreeBaseRelationNode) this);
			
		} else if (this.getClass().equals(SyntaxTreeGroupByNode.class) ) {
			result = group.transform((SyntaxTreeGroupByNode) this, childResults.get(0));
			
		} else if (this.getClass().equals(SyntaxTreeCrossNode.class) ) {
			result = cross.transform((SyntaxTreeCrossNode) this, childResults.get(0), childResults.get(1));
			
		} else if (this.getClass().equals(SyntaxTreeNodeDistinct.class) ) {
			result = distinct.transform((SyntaxTreeNodeDistinct) this, childResults.get(0));
			
		} else if (this.getClass().equals(SyntaxTreeProjectOperatorNode.class) ) {
			result = project.transform((SyntaxTreeProjectOperatorNode) this, childResults.get(0));
			
		} else if (this.getClass().equals(SyntaxTreeRenameNode.class) ) {
			result = rename.transform((SyntaxTreeRenameNode) this, childResults.get(0));
			
		} else if (this.getClass().equals(SyntaxTreeSortOperatorNode.class) ) {
			result = sort.transform((SyntaxTreeSortOperatorNode) this, childResults.get(0));
			
		} else if (this.getClass().equals(SyntaxTreeSelectionOperatorNode.class)){
			result = selection.transform((SyntaxTreeSelectionOperatorNode) this, childResults.get(0));
			
		} else if (this.getClass().equals(SyntaxTreeJoinNode.class)){
			result = join.transform((SyntaxTreeJoinNode) this, childResults.get(0), childResults.get(1));
			
		} else if (this.getClass().equals(SyntaxTreeAggregateNode.class)){
			result = aggregate.transform((SyntaxTreeAggregateNode)this, childResults.get(0));
			
		}else {
			throw new SQLSemanticException(SQLSemanticException.Type.InternalError, this.toString());
		}
		
		return result;
	}
	
	////
	//SCHEMA INSTANCIATION :: IMPLEMENTATION
	////
	
	private class InstanciateSchemaBase implements TransformBase<SyntaxTreeBaseRelationNode, SyntaxTreeNode>
	{
		private final Map<String, TableSchema> schemata;
		
		public InstanciateSchemaBase(List<TableSchema> schemata) {
			HashMap<String, TableSchema> schemataMap = new HashMap<String, TableSchema>();
			for (TableSchema schema : schemata) {
				schemataMap.put(schema.tableName, schema);
			}
			this.schemata = schemataMap;
		}
		
		@Override
		public SyntaxTreeBaseRelationNode transform(SyntaxTreeBaseRelationNode currentNode) throws SQLSemanticException {
			TableSchema schema = schemata.get(currentNode.name);
			if (schema == null) throw new SQLSemanticException(SQLSemanticException.Type.NoSuchTableException, currentNode.name);
			return new SyntaxTreeBaseRelationNode(schema);
		}
	}
	
	private class InstanciateSchemaJoin implements TransformBinary<SyntaxTreeJoinNode, SyntaxTreeNode>
	{
		@Override
		public SyntaxTreeJoinNode transform(SyntaxTreeJoinNode currentNode, SyntaxTreeNode leftChildResult, SyntaxTreeNode rightChildResult) {
			return new SyntaxTreeJoinNode(leftChildResult.schema.append(rightChildResult.schema), leftChildResult, rightChildResult, currentNode.leftIdentifier, currentNode.rightIdentifier);
		}
	}
	
	private class InstanciateSchemaCross implements TransformBinary<SyntaxTreeCrossNode, SyntaxTreeNode>
	{
		@Override
		public SyntaxTreeCrossNode transform(SyntaxTreeCrossNode currentNode, SyntaxTreeNode leftChildResult, SyntaxTreeNode rightChildResult) {
			return new SyntaxTreeCrossNode(leftChildResult.schema.append(rightChildResult.schema), leftChildResult, rightChildResult);
		}
	}
	
	private class InstanciateSchemaRename implements TransformUnary<SyntaxTreeRenameNode, SyntaxTreeNode>
	{
		@Override
		public SyntaxTreeNode transform(SyntaxTreeRenameNode currentNode, SyntaxTreeNode childResult) {
			return new SyntaxTreeRenameNode(childResult.schema.renameSchema(currentNode.name), childResult);
		}
	}
	
	private class InstanciateSchemaGroup implements TransformUnary<SyntaxTreeGroupByNode, SyntaxTreeNode>
	{
		@Override
		public SyntaxTreeNode transform(SyntaxTreeGroupByNode currentNode, SyntaxTreeNode childResult) {
			return new SyntaxTreeGroupByNode(childResult.schema, childResult, currentNode.groupByList());
		}
	}
	
	private class InstanciateSchemaDistinct implements TransformUnary<SyntaxTreeNodeDistinct, SyntaxTreeNode>
	{
		@Override
		public SyntaxTreeNode transform(SyntaxTreeNodeDistinct currentNode, SyntaxTreeNode childResult) {
			return new SyntaxTreeNodeDistinct(childResult.schema, childResult);
		}
	}
	
	private class InstanciateSchemaProject implements TransformUnary<SyntaxTreeProjectOperatorNode, SyntaxTreeNode>
	{
		@Override
		public SyntaxTreeNode transform(SyntaxTreeProjectOperatorNode currentNode, SyntaxTreeNode childResult) throws SQLSemanticException {
			assert(childResult != null);
			List<TableSchemaAttributeDetail> resolvedProjectionList = SyntaxTreeProjectOperatorNode.resolve(currentNode.getProjectionList(), childResult.schema);
			return new SyntaxTreeProjectOperatorNode(new TableSchema("", resolvedProjectionList), childResult, currentNode.getProjectionList());
		}
	}
	
	private class InstanciateSchemaAggregate implements TransformUnary<SyntaxTreeAggregateNode, SyntaxTreeNode>
	{
		@Override
		public SyntaxTreeNode transform(SyntaxTreeAggregateNode currentNode, SyntaxTreeNode childResult) throws SQLSemanticException {
			assert(childResult != null);
			
			SQLType type;
			
			//Case 1 Count
			if (currentNode.generatingToken.content.startsWith("count")) {
				//append with type int
				type = SQLType.INTEGER;
			//Case 2 Max
			} else if (currentNode.generatingToken.content.startsWith("max")) {
				//append with type of the column maximizing  over
				int index = childResult.schema.indexOf(currentNode.attributeIdentifier);
				type = childResult.schema.getAttributesTypes()[index];
				
			} else {
				throw new SQLSemanticException(SQLSemanticException.Type.InternalError);
			}
			
			TableSchemaAttributeDetail attribute = new TableSchemaAttributeDetail(currentNode.aggregateName, type, false, "");
			TableSchema extra = new TableSchema("", Arrays.asList(attribute));
			
			return new SyntaxTreeAggregateNode(childResult.schema.append(extra), currentNode.generatingToken, currentNode.aggregateName, childResult);

		}
	}
	
	private class InstanciateSchemaSelection implements TransformUnary<SyntaxTreeSelectionOperatorNode, SyntaxTreeNode>
	{
		@Override
		public SyntaxTreeNode transform(SyntaxTreeSelectionOperatorNode currentNode, SyntaxTreeNode childResult) throws SQLSemanticException {
			return new SyntaxTreeSelectionOperatorNode(childResult.schema, currentNode.getLeftValue(), currentNode.getOperator(), currentNode.getRightValue(), childResult);
		}
	}
	
	private class InstanciateSchemaSort implements TransformUnary<SyntaxTreeSortOperatorNode, SyntaxTreeNode>
	{
		@Override
		public SyntaxTreeNode transform(SyntaxTreeSortOperatorNode currentNode, SyntaxTreeNode childResult) {
			return new SyntaxTreeSortOperatorNode(childResult.schema, childResult, currentNode.getOrderStatement());
		}
	}
	
	////
	//TYPE INFERENCE IMPLEMENTATION
	////
	
	private class SelectionArgumentTypeInference implements TransformUnary<SyntaxTreeSelectionOperatorNode, Map<Integer, SQLType>> {

		@Override
		//Infers types of arguments of the form id=? or ?=id
		//If a selection between an argument and a literal occurs, or between two arguments, for now, a TypeError is thrown
		public Map<Integer, SQLType> transform(SyntaxTreeSelectionOperatorNode currentNode, Map<Integer, SQLType> childResult) throws SQLSemanticException {
			
			Map<Integer,SQLType> newArguments = new TreeMap<Integer,SQLType>();
			
			if (currentNode.getLeftValue().generatingToken.tokenClass == SQLToken.SQLTokenClass.ARGUMENT) {//Case ?=A
				if (currentNode.schema.hasAttribute(currentNode.rightIdentifier)) {
					SQLType argumentType = currentNode.schema.getAttributesTypes()[currentNode.schema.indexOf(currentNode.rightIdentifier)];
					assert(argumentType != null);
					newArguments.put(currentNode.getLeftValue().generatingToken.identifier, argumentType);
				} else {
					throw new SQLSemanticException(SQLSemanticException.Type.TypeError);
				}
			}
			if (currentNode.getRightValue().generatingToken.tokenClass == SQLToken.SQLTokenClass.ARGUMENT) {//Case A=?
				if (currentNode.schema.hasAttribute(currentNode.leftIdentifier)) {
					SQLType argumentType = currentNode.schema.getAttributesTypes()[currentNode.schema.indexOf(currentNode.leftIdentifier)];
					assert(argumentType != null);
					newArguments.put(currentNode.getRightValue().generatingToken.identifier, argumentType);	
				} else {
					throw new SQLSemanticException(SQLSemanticException.Type.TypeError);
				}
			}
			return appendMaps(newArguments, childResult);
		}

		
	}
	
	private class NullTransform<S, T> implements TransformBase<S, T> {
		@Override
		public T transform(S currentNode) throws SQLSemanticException {
			return null;
		}	
	}
	
	private class AppendMapsTransform<S, K, V> implements TransformBinary <S, Map<K, V>> {

		@Override
		public Map<K, V> transform(S currentNode, Map<K, V> leftChildResult, Map<K, V> rightChildResult) throws SQLSemanticException {
			return appendMaps(leftChildResult, rightChildResult);
		}
	}
	
	private class IdentityTransform<S, T> implements TransformUnary <S, T> {
		@Override
		public T transform(S currentNode, T childResult) throws SQLSemanticException {
			return childResult;
		}
	}
	
	private <K,V>  Map<K, V> appendMaps(Map<K, V> leftChildResult, Map<K, V> rightChildResult) throws SQLSemanticException {
		if (leftChildResult == null) return rightChildResult;
		if (rightChildResult == null) return leftChildResult;
		Map<K, V> result =  new TreeMap<K, V>();
		result.putAll(leftChildResult);
		result.putAll(rightChildResult);
		return result;
	}
	
	
	////
	//OVERRIDING OBJECT
	////
	
	@Override
	public String toString()
	{
		String info = infoToString();
		String result = this.getClass().getSimpleName().substring("SyntaxTree".length());
		if (info != null && info.length() > 0) {
		result = result + " (" + infoToString() + ")";
		}
		if (children.length > 0) {
			result += "[ ";
			for (SyntaxTreeNode child : children) {
				if (child != null) {
					result += child.toString() + " ";
				}
			}
			result += "]";
		}
		return  result;
	}
	
	protected String infoToString()
	{
		return "";
	}
	
}
