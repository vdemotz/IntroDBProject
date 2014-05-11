package ch.ethz.inf.dbproject.sqlRevisited.Parser;

public class SyntaxTreeProjectAndAggregateOperatorNode extends SyntaxTreeNode {
	
	public SyntaxTreeProjectAndAggregateOperatorNode(SyntaxTreeNode result, SyntaxTreeListNode<SyntaxTreeNode> projectOnto) {
		super(result, projectOnto);
	}
	
}
