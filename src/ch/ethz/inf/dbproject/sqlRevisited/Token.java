package ch.ethz.inf.dbproject.sqlRevisited;

public class Token {

	public enum TokenClass {
		ARGUMENT,
		COMPARATOR,
		BOOLEAN_CONNECTIVE,
		OPEN_PARENTHESIS,
		CLOSE_PARENTHESIS;
		//ETC...
	}
	
	public final String content;
	public final TokenClass tokenClass;
	
	public Token (TokenClass tokenClass, String content) {
		this.content = content;
		this.tokenClass = tokenClass;
	}
}
