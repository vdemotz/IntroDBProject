package ch.ethz.inf.dbproject.sqlRevisited.Parser;

import ch.ethz.inf.dbproject.sqlRevisited.AttributedTableSchema;

public class SyntaxTreeRenameTableNode extends SyntaxTreeNode {
	
	String name;
	
	SyntaxTreeRenameTableNode(SyntaxTreeNode table, String newName) {
		super(table);
		this.name = newName;
	}

	public SyntaxTreeRenameTableNode(AttributedTableSchema schema, SyntaxTreeNode table) {
		super(schema, table);
		this.name = schema.tableName;
		assert(schema != null);
	}
	
}
