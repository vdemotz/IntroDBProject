package ch.ethz.inf.dbproject.sqlRevisited;

public class SQLSyntaxTreeCrossNode extends SyntaxTreeNode {
	
	public SyntaxTreeNode getLeft() {
		return children[0];
	}
	
	public SyntaxTreeNode getRight() {
		return children[1];
	}
	
	public SQLSyntaxTreeCrossNode(SyntaxTreeNode left, SyntaxTreeNode right) {
		super(left, right);
	}
	
}
