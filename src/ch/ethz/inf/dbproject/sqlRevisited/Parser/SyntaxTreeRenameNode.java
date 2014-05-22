package ch.ethz.inf.dbproject.sqlRevisited.Parser;

import java.util.LinkedList;
import java.util.List;

import ch.ethz.inf.dbproject.sqlRevisited.TableSchema;
import ch.ethz.inf.dbproject.sqlRevisited.TableSchemaAttributeDetail;

public class SyntaxTreeRenameNode extends SyntaxTreeNode {
	
	public final String name;
	
	SyntaxTreeNode getChild() {
		return children.get(0);
	}

	SyntaxTreeRenameNode(SyntaxTreeNode table, String newName) {
		super(table);
		this.name = newName;
	}

	public SyntaxTreeRenameNode(TableSchema schema, SyntaxTreeNode child) {
		super(schema, child);
		this.name = schema.tableName;
		assert(schema != null);
	}
	
	@Override
	protected String infoToString()
	{
		return name.toUpperCase();
	}
	
}
