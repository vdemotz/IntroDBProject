package ch.ethz.inf.dbproject.sqlRevisited.Parser;

import ch.ethz.inf.dbproject.sqlRevisited.TableSchema;

public class SyntaxTreeNodeDistinct extends SyntaxTreeNode {
	
	public SyntaxTreeNode child() {
		return children[0];
	}
	
	public SyntaxTreeNodeDistinct(SyntaxTreeNode body) {
		super(body);
		assert (body != null);
	}

	public SyntaxTreeNodeDistinct(SyntaxTreeNode child, TableSchema schema) {
		super(schema, child);
		assert(schema != null);
	}
}
