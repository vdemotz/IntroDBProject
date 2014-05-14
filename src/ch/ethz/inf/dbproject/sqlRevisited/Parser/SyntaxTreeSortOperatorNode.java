package ch.ethz.inf.dbproject.sqlRevisited.Parser;

import ch.ethz.inf.dbproject.sqlRevisited.AttributedTableSchema;

public class SyntaxTreeSortOperatorNode extends SyntaxTreeNode {

	
	@SuppressWarnings("unchecked")
	SyntaxTreeListNode<SyntaxTreeOrderingNode> getOrderStatement()
	{
		return (SyntaxTreeListNode<SyntaxTreeOrderingNode>) children[1];
	}
	
	public SyntaxTreeSortOperatorNode(SyntaxTreeNode innerStatement, SyntaxTreeListNode<SyntaxTreeOrderingNode> orderStatement) {
		super(innerStatement, orderStatement);
	}

	public SyntaxTreeSortOperatorNode(AttributedTableSchema schema, SyntaxTreeNode child, SyntaxTreeListNode<SyntaxTreeOrderingNode> orderStatement) {
		super(schema, child, orderStatement);
	}

}
