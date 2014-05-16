package ch.ethz.inf.dbproject.sqlRevisited.Parser;

import ch.ethz.inf.dbproject.sqlRevisited.TableSchema;

public abstract class SyntaxTreeBinaryNode extends SyntaxTreeNode{

	public SyntaxTreeBinaryNode(SyntaxTreeNode left, SyntaxTreeNode right) {
		super(left, right);
	}

	public SyntaxTreeBinaryNode(TableSchema schema, SyntaxTreeNode left, SyntaxTreeNode right) {
		super(schema, left, right);
		assert(schema != null);
		assert(left != null);
		assert(right != null);
	}

	public final SyntaxTreeNode getLeft() {
		return children.get(0);
	}
	
	public final SyntaxTreeNode getRight() {
		return children.get(1);
	}
	
	public SyntaxTreeBinaryNode copyWithLeftChild(SyntaxTreeNode left) {
		return copyWithChildren(left, getRight());
	}
	
	public SyntaxTreeBinaryNode copyWithRightChild(SyntaxTreeNode right) {
		return copyWithChildren(getLeft(), right);
	}
	
	abstract public SyntaxTreeBinaryNode copyWithChildren(SyntaxTreeNode left, SyntaxTreeNode right);
	
}
