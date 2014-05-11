package ch.ethz.inf.dbproject.sqlRevisited.Parser;

public class SyntaxTreeCrossNode extends SyntaxTreeNode {
	
	public SyntaxTreeNode getLeft() {
		return children[0];
	}
	
	public SyntaxTreeNode getRight() {
		return children[1];
	}
	
	public SyntaxTreeCrossNode(SyntaxTreeNode left, SyntaxTreeNode right) {
		super(left, right);
	}
	
}
