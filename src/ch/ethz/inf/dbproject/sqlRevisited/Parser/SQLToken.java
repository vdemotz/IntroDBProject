package ch.ethz.inf.dbproject.sqlRevisited.Parser;

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
		AND ("\\band\\b"),
		UNION ("\\bunion\\b"),
		INSERTINTO ("\\binsert\\s+into\\b"),
		UPDATE ("\\bupdate\\b"),
		SET("\\bset\\b"),
		DELETE ("\\bdelete\\s+from\\b"),
		AS ("\\bas\\b"),
		VALUES ("\\bvalues\\b"),
		ORDERDIRECTION ("\\b(desc|asc)\\b"),
		//BINARYCONNECTIVE ("\\b(and|or)\\b"),
		//UNARYCONNECTIVE ("\\bnot\\b"),
		//data
		BOOL ("\\b(true|false)\\b"),
		AGGREGATE ("\\b(count|max)\\([*a-z]+(\\.[*a-z])?\\)"),
		QID ("\\b[a-z]+\\.([a-z]+\\b)"),
		QSTARID ("\\b[a-z]+\\.\\*"),
		UID ("\\b[a-z]+\\b"),
		LITERAL ("(\\d+\\b|'(.*)')"),
		ARGUMENT ("\\?"),
		STAR ("\\*"),
		//compare and set
		EQUAL ("="),
		//compare
		COMPARATOR ("<=|>=|<|>|\\blike\\b"),
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
	
	public final int identifier;//0 by default, may be used to distinguish tokens that occur multiple times (like ARGUMENT)
	public final String content;
	public final SQLTokenClass tokenClass;
	
	public SQLToken (SQLTokenClass tokenClass, String content) {
		identifier = 0;
		this.content = content;
		this.tokenClass = tokenClass;
	}
	
	public SQLToken (SQLTokenClass tokenClass, String content, int identifier) {
		this.identifier = identifier;
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
		String result = "(" + tokenClass.name() + ", '" + content.toString();
		if (identifier != 0) result += ", " + identifier;
		return result + "')";
	}
}
