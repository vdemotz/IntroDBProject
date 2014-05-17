package ch.ethz.inf.dbproject.sqlRevisited.Parser;

import ch.ethz.inf.dbproject.sqlRevisited.TableSchema;

public class SyntaxTreeCrossNode extends SyntaxTreeBinaryNode {
	
	public SyntaxTreeCrossNode(SyntaxTreeNode left, SyntaxTreeNode right) {
		super(left, right);
	}

	public SyntaxTreeCrossNode(TableSchema schema,SyntaxTreeNode left, SyntaxTreeNode right) {
		super(schema, left, right);
	}

	@Override
	public SyntaxTreeBinaryNode copyWithChildren(SyntaxTreeNode left, SyntaxTreeNode right) {
		return new SyntaxTreeCrossNode(schema, left, right);
	}
	
}
