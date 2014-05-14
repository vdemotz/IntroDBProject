package ch.ethz.inf.dbproject.sqlRevisited.Parser;

import ch.ethz.inf.dbproject.sqlRevisited.TableSchema;

public class SyntaxTreeProjectAndAggregateOperatorNode extends SyntaxTreeNode {
	
	@SuppressWarnings("unchecked")
	SyntaxTreeListNode<SyntaxTreeNode> getProjectionList()
	{
		return (SyntaxTreeListNode<SyntaxTreeNode>) children[1];
	}
	
	public SyntaxTreeProjectAndAggregateOperatorNode(SyntaxTreeNode child, SyntaxTreeListNode<SyntaxTreeNode> projectOnto) {
		super(child, projectOnto);
		assert(child != null);
		assert(projectOnto != null);
	}

	public SyntaxTreeProjectAndAggregateOperatorNode(TableSchema schema, SyntaxTreeNode child, SyntaxTreeListNode<SyntaxTreeNode> projectionList) {
		super(schema, child, projectionList);
		assert(schema != null);
		assert(child != null);
		assert(projectionList != null);
	}
	
}
