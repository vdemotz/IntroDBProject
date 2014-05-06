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
		//BINARYCONNECTIVE ("\\b(and|or)\\b"),
		//UNARYCONNECTIVE ("\\bnot\\b"),
		//data
		BOOL ("\\b(true|false)\\b"),
		AGGREGATE ("\\b(count|max)\\([*a-z]+\\)"),
		QID ("\\b[a-z]+\\.([a-z]+\\b)"),
		QSTARID ("\\b[a-z]+\\.\\*"),
		UID ("\\b[a-z]+\\b"),
		LITERAL ("(\\d+\\b|'(.*)')"),
		ARGUMENT ("\\?"),
		STAR ("\\*"),
		//compare
		COMPARATOR ("<=|>=|<|=|>|\\blike\\b"),
		//Structure
		COMMA (","),
		OPENPAREN ("\\("),
		CLOSEPAREN ("\\)"),
		WHITESPACE ("\\s+"),
		//if there's no other match, go into "panic mode"
		//ERROR (".+\\)"),//try to recover
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
		String result = "(?i)";
		for (SQLTokenClass tokenClass : SQLToken.SQLTokenClass.values()) {
			result += String.format("(?<%s>%s)|", tokenClass.name(), tokenClass.pattern);
		}
		return result.substring(0, result.length()-1);
	}
	
	public String toString() {
		return "(" + tokenClass.name() + ", '" + content.toString() + "')";
	}
}
