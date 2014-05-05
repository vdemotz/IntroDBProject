package ch.ethz.inf.dbproject.sqlRevisited;

public class SQLToken {

	public enum SQLTokenClass {
		//keywords
		SELECT ("\\bselect\\b"),
		FROM ("\\bfrom\\b"),
		WHERE ("\\bwhere\\b"),
		GROUPBY ("\\bgroup\\s+by\\b"),
		ORDERBY ("\\border\\s+by\\b"),
		DISTINCT ("\\bdistinct\\b"),
		UNIONALL ("\\bunion\\s+all\\b"),
		UNION ("\\bunion\\b"),
		INSERTINTO ("\\binsert\\s+into\\b"),
		UPDATE ("\\bupdate\\b"),
		SET("\\bset\\b"),
		DELETE ("\\bdelete\\b"),
		AS ("\\bas\\b"),
		AND ("\\band\\b"),
		ORDERDIRECTION ("\\b(desc|asc)\\b"),
		//BOOLEAN_BINARY_CONNECTIVE ("\\b(and|or)\\b"),
		//BOOLEAN_UNARY_CONNECTIVE ("\\bnot\\b"),
		//data
		QUALIFIEDID ("\\b[a-zA-Z]+\\.(\\*|[a-zA-Z]+\\b)"),//unsure about .
		UNQUALIFIEDID ("\\b[a-zA-Z]+\\b"),
		LITERAL ("\\b(\\d+|'(.*)')\\b"),
		AGGREGATE ("\\b(count|max)\\([a-zA-Z]+\\)\\b"),
		ARGUMENT ("\\?"),
		STAR ("\\*"),
		//compare
		COMPARATOR ("<=|>=|<|=|>|\\blike\\b"),
		//Structure
		COMMA (","),
		OPENPAREN ("\\("),
		CLOSEPAREN ("\\)"),
		WHITESPACE ("\\s+"),
		//if none other match, error (go into "panic mode")
		ERROR (".+");
		
		public final String pattern;
		
		SQLTokenClass(String pattern) {
			this.pattern = pattern;
		}
	}
	
	public final String content;
	public final SQLTokenClass tokenClass;
	
	public SQLToken (SQLTokenClass tokenClass, String content) {
		this.content = content;
		this.tokenClass = tokenClass;
	}
	
	public static String getPattern() {
		String result = "";
		for (SQLTokenClass tokenClass : SQLToken.SQLTokenClass.values()) {
			result += String.format("|(?<%s>%s)", tokenClass.name(), tokenClass.pattern);
		}
		return result.substring(1);
	}
	
	public String toString() {
		return "( " + tokenClass.name() + ", '" + content.toString() + "')";
	}
}
