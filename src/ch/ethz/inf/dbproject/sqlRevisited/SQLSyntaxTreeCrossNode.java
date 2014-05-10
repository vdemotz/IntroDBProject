package ch.ethz.inf.dbproject.sqlRevisited;

public class SQLSyntaxTreeCrossNode extends SyntaxTreeNode {
	
	SyntaxTreeNode getLeft() {
		return children[0];
	}
	
	SyntaxTreeNode getRight() {
		return children[1];
	}
	
	SQLSyntaxTreeCrossNode(SyntaxTreeNode left, SyntaxTreeNode right) {
		super(left, right);
	}
	
}
