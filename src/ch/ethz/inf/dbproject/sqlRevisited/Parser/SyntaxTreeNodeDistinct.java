package ch.ethz.inf.dbproject.sqlRevisited.Parser;

public class SyntaxTreeNodeDistinct extends SyntaxTreeNode {
	
	public SyntaxTreeNode child() {
		return children[0];
	}
	
	public SyntaxTreeNodeDistinct(SyntaxTreeNode body) {
		super(body);
		assert (body != null);
	}
	
}
