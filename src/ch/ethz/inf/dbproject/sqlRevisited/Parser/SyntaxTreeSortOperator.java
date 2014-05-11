package ch.ethz.inf.dbproject.sqlRevisited.Parser;

public class SyntaxTreeSortOperator extends SyntaxTreeNode {

	public SyntaxTreeSortOperator(SyntaxTreeNode innerStatement, SyntaxTreeListNode<SyntaxTreeOrderingNode> orderStatement) {
		super(innerStatement, orderStatement);
	}

}
