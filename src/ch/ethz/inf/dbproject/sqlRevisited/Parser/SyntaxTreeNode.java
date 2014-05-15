package ch.ethz.inf.dbproject.sqlRevisited.Parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


import ch.ethz.inf.dbproject.Pair;
import ch.ethz.inf.dbproject.sqlRevisited.TableSchema;
import ch.ethz.inf.dbproject.sqlRevisited.TableSchemaAttributeDetail;

public class SyntaxTreeNode {

	public final TableSchema schema;
	protected final SyntaxTreeNode[] children;
	
	SyntaxTreeNode(TableSchema schema, SyntaxTreeNode...children) {
		this.children = children;
		this.schema = schema;
	}
	
	SyntaxTreeNode(SyntaxTreeNode...children) {
		this.children = children;
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
		return fold(new InstanciateSchemaBase(schemata), new InstanciateSchemaCross(), new InstanciateSchemaGroup(), new InstanciateSchemaDistinct(),
					new InstanciateSchemaProjectAggregate(), new InstanciateSchemaRename(), new InstanciateSchemaSelection(), new InstanciateSchemaSort());
	}
	
	/**
	 * Rewrites the syntax tree to a semantically equivalent, but one that can perform queries faster
	 * This involves pushing down selection as far as possible and introducing joins where possible
	 * @return a semantically equivalent syntax tree
	 * @throws SQLSemanticException
	 */
	public SyntaxTreeNode rewrite() throws SQLSemanticException
	{
		return fold(new RewriteBase(), new RewriteCross(), new RewriteGroup(), new RewriteDistinct(),
					new RewriteProjectAndAggregate(), new RewriteRename(), new RewriteSelection(), new RewriteSort());
	}
	
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
			//TODO: if possible, make join operator
			return newRoot;
		}
		
		public SyntaxTreeNode pushDown(SyntaxTreeSelectionOperatorNode node) throws SQLSemanticException {
			if (node.getChild().getClass().equals(SyntaxTreeSelectionOperatorNode.class)) {//Case selection : always push down
				SyntaxTreeSelectionOperatorNode child = (SyntaxTreeSelectionOperatorNode)node.getChild();
				SyntaxTreeSelectionOperatorNode nodePointingToGrandchild = node.copyWithChild(child.getChild());
				return child.copyWithChild(pushDown(nodePointingToGrandchild));//recursively push down
				
			} else if(node.getChild().getClass().equals(SyntaxTreeCrossNode.class)) {//Case cross : push down left or right, if possible
				SyntaxTreeCrossNode child = (SyntaxTreeCrossNode)node.getChild();
				
				boolean leftChildHasLeftAttribute = false;
				boolean leftChildHasRightAttribute = false;
				boolean rightChildHasRightAttribute = false;
				boolean rightChildHasLeftAttribute = false;
				
				if (node.getLeftValue().generatingToken.tokenClass == SQLToken.SQLTokenClass.QID || node.getLeftValue().generatingToken.tokenClass == SQLToken.SQLTokenClass.UID) {
					Pair<String, String> leftFragments = node.getLeftValue().generatingToken.getFragmentsForIdentifier();
					leftChildHasLeftAttribute = child.getLeft().schema.hasAttribute(leftFragments);
					rightChildHasLeftAttribute = child.getRight().schema.hasAttribute(leftFragments);
				}
				if (node.getRightValue().generatingToken.tokenClass == SQLToken.SQLTokenClass.QID || node.getRightValue().generatingToken.tokenClass == SQLToken.SQLTokenClass.UID) {
					Pair<String, String> rightFragments = node.getRightValue().generatingToken.getFragmentsForIdentifier();	
					leftChildHasRightAttribute = child.getLeft().schema.hasAttribute(rightFragments);
					rightChildHasRightAttribute = child.getRight().schema.hasAttribute(rightFragments);	
				}
				
				if (!rightChildHasRightAttribute && !rightChildHasLeftAttribute) {//Sub-Case Push Left
					SyntaxTreeSelectionOperatorNode nodePointingToLeftGrandchild = node.copyWithChild(child.getLeft());
					return child.copyWithLeftChild(pushDown(nodePointingToLeftGrandchild));//recursively push down the left subtree and reassemble
						
				} else if (!leftChildHasRightAttribute && !leftChildHasLeftAttribute) {//Sub-Case Push Right
						SyntaxTreeSelectionOperatorNode nodePointingToRightGrandchild = node.copyWithChild(child.getRight());
						return child.copyWithRightChild(pushDown(nodePointingToRightGrandchild));//recursively push down the right subtree and reassemble
						
				} else {
					return node;
				}
				
			} else {
				return node;
			}
		}
	}
	
	private class RewriteRename implements TransformUnary<SyntaxTreeRenameTableNode, SyntaxTreeNode> {
		@Override
		public SyntaxTreeNode transform(SyntaxTreeRenameTableNode currentNode, SyntaxTreeNode childResult) throws SQLSemanticException {
			return new SyntaxTreeRenameTableNode(currentNode.schema, childResult);
		}
	}
	
	private class RewriteProjectAndAggregate implements TransformUnary<SyntaxTreeProjectAndAggregateOperatorNode, SyntaxTreeNode> {
		@Override
		public SyntaxTreeNode transform(SyntaxTreeProjectAndAggregateOperatorNode currentNode, SyntaxTreeNode childResult) throws SQLSemanticException {
			return new SyntaxTreeProjectAndAggregateOperatorNode(currentNode.schema, childResult, currentNode.getProjectionList());
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
	
	////
	//PSEUDO-FOLD OPERATORS
	////
	
	protected interface TransformBase<S, T> {
		T transform(S currentNode) throws SQLSemanticException;
	}
	
	protected interface TransformUnary<S, T> {
		T transform(S currentNode, T childResult) throws SQLSemanticException;
	}
	
	protected interface TransformBinary<S, T> {
		T transform(S currentNode, T leftChildResult, T rightChildResult) throws SQLSemanticException;
	}
	
	/**
	 * Fold operation over all non-list, non-identifier nodes of the subtree rooted at this node
	 * It is strongly encouraged not to modify any of the nodes, but instead to create a new copy of the nodes
	 * @param base
	 * @param cross
	 * @param group
	 * @param distinct
	 * @param projectAggregate
	 * @param rename
	 * @param selection
	 * @param sort
	 * @return
	 * @throws SQLSemanticException
	 */
	protected <T> T fold(TransformBase<SyntaxTreeBaseRelationNode, T> base,
					  TransformBinary<SyntaxTreeCrossNode, T> cross,
					  TransformUnary<SyntaxTreeGroupByNode, T> group,
					  TransformUnary<SyntaxTreeNodeDistinct, T> distinct,
					  TransformUnary<SyntaxTreeProjectAndAggregateOperatorNode, T> projectAggregate,
					  TransformUnary<SyntaxTreeRenameTableNode, T> rename,
					  TransformUnary<SyntaxTreeSelectionOperatorNode, T> selection,
					  TransformUnary<SyntaxTreeSortOperatorNode, T> sort
					  ) throws SQLSemanticException
	{
		ArrayList<T> childResults = new ArrayList<T>();
		for (SyntaxTreeNode child : children) {
			if (!child.getClass().equals(SyntaxTreeListNode.class) && !child.getClass().equals(SyntaxTreeIdentifierNode.class)) {//do not fold over list nodes, identifiers
				childResults.add (child.fold(base, cross, group, distinct, projectAggregate, rename, selection, sort));
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
			
		} else if (this.getClass().equals(SyntaxTreeProjectAndAggregateOperatorNode.class) ) {
			result = projectAggregate.transform((SyntaxTreeProjectAndAggregateOperatorNode) this, childResults.get(0));
			
		} else if (this.getClass().equals(SyntaxTreeRenameTableNode.class) ) {
			result = rename.transform((SyntaxTreeRenameTableNode) this, childResults.get(0));
			
		} else if (this.getClass().equals(SyntaxTreeSortOperatorNode.class) ) {
			result = sort.transform((SyntaxTreeSortOperatorNode) this, childResults.get(0));
			
		} else if (this.getClass().equals(SyntaxTreeSelectionOperatorNode.class)){
			result = selection.transform((SyntaxTreeSelectionOperatorNode) this, childResults.get(0));
			
		} else {
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
	
	private class InstanciateSchemaCross implements TransformBinary<SyntaxTreeCrossNode, SyntaxTreeNode>
	{
		@Override
		public SyntaxTreeCrossNode transform(SyntaxTreeCrossNode currentNode, SyntaxTreeNode leftChildResult, SyntaxTreeNode rightChildResult) {
			return new SyntaxTreeCrossNode(leftChildResult.schema.append(rightChildResult.schema), leftChildResult, rightChildResult);
		}
	}
	
	private class InstanciateSchemaRename implements TransformUnary<SyntaxTreeRenameTableNode, SyntaxTreeNode>
	{
		@Override
		public SyntaxTreeNode transform(SyntaxTreeRenameTableNode currentNode, SyntaxTreeNode childResult) {
			return new SyntaxTreeRenameTableNode(childResult.schema.renameSchema(currentNode.name), childResult);
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
	
	private class InstanciateSchemaProjectAggregate implements TransformUnary<SyntaxTreeProjectAndAggregateOperatorNode, SyntaxTreeNode>
	{
		@Override
		public SyntaxTreeNode transform(SyntaxTreeProjectAndAggregateOperatorNode currentNode, SyntaxTreeNode childResult) throws SQLSemanticException {
			assert(childResult != null);
			List<TableSchemaAttributeDetail> resolvedProjectionList = SyntaxTreeProjectAndAggregateOperatorNode.resolve(currentNode.getProjectionList(), childResult.schema);
			return new SyntaxTreeProjectAndAggregateOperatorNode(new TableSchema("", resolvedProjectionList), childResult, currentNode.getProjectionList());
		}
	}
	
	private class InstanciateSchemaSelection implements TransformUnary<SyntaxTreeSelectionOperatorNode, SyntaxTreeNode>
	{
		@Override
		public SyntaxTreeNode transform(SyntaxTreeSelectionOperatorNode currentNode, SyntaxTreeNode childResult) {
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
