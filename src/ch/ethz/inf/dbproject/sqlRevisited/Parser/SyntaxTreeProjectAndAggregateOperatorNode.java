package ch.ethz.inf.dbproject.sqlRevisited.Parser;

import ch.ethz.inf.dbproject.sqlRevisited.AttributedTableSchema;

public class SyntaxTreeProjectAndAggregateOperatorNode extends SyntaxTreeNode {
	
	@SuppressWarnings("unchecked")
	SyntaxTreeListNode<SyntaxTreeNode> getProjectionList()
	{
		return (SyntaxTreeListNode<SyntaxTreeNode>) children[1];
	}
	
	public SyntaxTreeProjectAndAggregateOperatorNode(SyntaxTreeNode child, SyntaxTreeListNode<SyntaxTreeNode> projectOnto) {
		super(child, projectOnto);
	}

	public SyntaxTreeProjectAndAggregateOperatorNode(AttributedTableSchema schema, SyntaxTreeNode childResult, SyntaxTreeListNode<SyntaxTreeNode> projectionList) {
		super(schema, childResult, projectionList);
	}
	
}
