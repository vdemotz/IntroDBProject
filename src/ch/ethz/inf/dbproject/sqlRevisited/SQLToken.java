package ch.ethz.inf.dbproject.sqlRevisited;

public class SQLToken {

	public enum TokenClass {
		ARGUMENT ("\\?"),
		COMPARATOR ("[<|<=|=|>=]"),
		BOOLEAN_BINARY_CONNECTIVE ("and|or"),
		BOOLEAN_UNARY_CONNECTIVE ("not"),
		OPEN_PARENTHESIS ("\\("),
		CLOSE_PARENTHESIS ("\\)"),
		UNQUALIFIED_ID,
		QUALIFIED_ID,
		LITERAL,
		GROUP_OPERATOR,
		SELECT,
		FROM,
		WHERE,
		GROUP_BY,
		ORDER_BY,
		DISTINCT,
		UNION_ALL,
		INSERT_INTO,
		UPDATE,
		DELETE,
		COMMA (","),
		AS ("as"),
		WHITESPACE,
		ERROR;//if none other match, error
		
		public final String pattern;
		
		TokenClass(String pattern) {
			this.pattern = pattern;
		}
		TokenClass() {
			pattern = null;
		}
	}
	
	public final String content;
	public final TokenClass tokenClass;
	
	public SQLToken (TokenClass tokenClass, String content) {
		this.content = content;
		this.tokenClass = tokenClass;
	}
}
