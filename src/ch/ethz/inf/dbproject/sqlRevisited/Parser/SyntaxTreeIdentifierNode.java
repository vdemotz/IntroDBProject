package ch.ethz.inf.dbproject.sqlRevisited.Parser;


public class SyntaxTreeIdentifierNode extends SyntaxTreeNode {
	
	public final SQLToken generatingToken;
	//String tableName;
	//String attributeName;
	
	public SyntaxTreeIdentifierNode(SQLToken token) {
		generatingToken = token;
	}

}
