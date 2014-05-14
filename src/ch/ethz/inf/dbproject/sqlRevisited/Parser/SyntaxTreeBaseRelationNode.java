package ch.ethz.inf.dbproject.sqlRevisited.Parser;

import ch.ethz.inf.dbproject.sqlRevisited.AttributedTableSchema;

public class SyntaxTreeBaseRelationNode extends SyntaxTreeNode{

	public final String name;
	
	public SyntaxTreeBaseRelationNode(String name) {
		this.name = name;
	}

	public SyntaxTreeBaseRelationNode(AttributedTableSchema schema) {
		super(schema);
		this.name = schema.tableName;
	}
}
