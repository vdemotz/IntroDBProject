package ch.ethz.inf.dbproject.sqlRevisited;

public class SQLSyntaxTreeIdentifierNode extends SyntaxTreeNode {
	
	SQLToken generatingToken;
	//String tableName;
	//String attributeName;
	
	protected SQLSyntaxTreeIdentifierNode(SQLToken token) {
		generatingToken = token;
	}

}
