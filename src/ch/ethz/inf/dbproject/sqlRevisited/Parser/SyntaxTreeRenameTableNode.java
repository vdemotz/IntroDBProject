package ch.ethz.inf.dbproject.sqlRevisited.Parser;

import java.util.LinkedList;
import java.util.List;

import ch.ethz.inf.dbproject.sqlRevisited.TableSchema;
import ch.ethz.inf.dbproject.sqlRevisited.TableSchemaAttributeDetail;

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
