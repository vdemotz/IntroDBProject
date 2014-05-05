package ch.ethz.inf.dbproject.sqlRevisited;

public class SQLToken {

	public enum TokenClass {
		ARGUMENT,
		COMPARATOR,
		BOOLEAN_CONNECTIVE,
		OPEN_PARENTHESIS,
		CLOSE_PARENTHESIS,
		//ETC...
		ERROR;//if none other match, error
	}
	
	public final String content;
	public final TokenClass tokenClass;
	
	public SQLToken (TokenClass tokenClass, String content) {
		this.content = content;
		this.tokenClass = tokenClass;
	}
}
