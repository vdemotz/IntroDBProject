package ch.ethz.inf.dbproject.sqlRevisited.Parser;

import ch.ethz.inf.dbproject.sqlRevisited.TableSchema;

public class SyntaxTreeRenameTableNode extends SyntaxTreeNode {
	
	String name;
	
	SyntaxTreeRenameTableNode(SyntaxTreeNode table, String newName) {
		super(table);
		this.name = newName;
	}

	public SyntaxTreeRenameTableNode(TableSchema schema, SyntaxTreeNode table) {
		super(schema, table);
		this.name = schema.tableName;
		assert(schema != null);
	}
	
}
