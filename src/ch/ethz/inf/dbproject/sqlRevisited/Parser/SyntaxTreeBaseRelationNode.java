package ch.ethz.inf.dbproject.sqlRevisited.Parser;

import ch.ethz.inf.dbproject.sqlRevisited.TableSchema;

public class SyntaxTreeBaseRelationNode extends SyntaxTreeNode{

	public final String name;
	
	public SyntaxTreeBaseRelationNode(String name) {
		this.name = name;
		assert(name != null);
	}

	public SyntaxTreeBaseRelationNode(TableSchema schema) {
		super(schema);
		this.name = schema.tableName;
		assert(schema != null);
		assert(name != null);
	}
}
