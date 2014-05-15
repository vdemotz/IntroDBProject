package ch.ethz.inf.dbproject.sqlRevisited.Parser;

import ch.ethz.inf.dbproject.sqlRevisited.TableSchema;

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

	public SyntaxTreeCrossNode(TableSchema schema,SyntaxTreeNode left, SyntaxTreeNode right) {
		super(schema, left, right);
		assert(schema != null);
		assert(left != null);
		assert(right != null);
	}
	
}
