package ch.ethz.inf.dbproject.sqlRevisited.Parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.ethz.inf.dbproject.sqlRevisited.AttributedTableSchema;
import ch.ethz.inf.dbproject.sqlRevisited.TableSchema;

public class SyntaxTreeNode implements Cloneable {

	protected final AttributedTableSchema schema;
	protected final SyntaxTreeNode[] children;
	
	SyntaxTreeNode(AttributedTableSchema schema, SyntaxTreeNode...children) {
		this.children = children;
		this.schema = schema;
	}
	
	SyntaxTreeNode(SyntaxTreeNode...children) {
		this.children = children;
		this.schema = null;
	}
	
	public SyntaxTreeNode instanciateWithSchemata(List<TableSchema> schemata) throws SQLSemanticException {
		return fold(new InstanciateSchemaBase(schemata), new InstanciateSchemaCross(), new InstanciateSchemaGroup(), new InstanciateSchemaDistinct(),
				new InstanciateSchemaProjectAggregate(), new InstanciateSchemaRename(), new InstanciateSchemaSelection(), new InstanciateSchemaSort());
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
	 * It is strongly encourages not to modify any of the nodes, but instead to create a new copy of the nodes
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
			return new SyntaxTreeBaseRelationNode(new AttributedTableSchema(schema));
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
			return new SyntaxTreeNodeDistinct(childResult, childResult.schema);
		}
	}
	
	private class InstanciateSchemaProjectAggregate implements TransformUnary<SyntaxTreeProjectAndAggregateOperatorNode, SyntaxTreeNode>
	{
		@Override
		public SyntaxTreeNode transform(SyntaxTreeProjectAndAggregateOperatorNode currentNode, SyntaxTreeNode childResult) {
			assert(childResult != null);
			return new SyntaxTreeProjectAndAggregateOperatorNode(childResult.schema, childResult, currentNode.getProjectionList());
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
	
	public String toString()
	{
		String result = this.getClass().getSimpleName() + "[ ";
		for (SyntaxTreeNode child : children) {
			if (child != null) {
				result += child.toString() + " ";
			}
		}
		return  result + "]";
	}
	
}
