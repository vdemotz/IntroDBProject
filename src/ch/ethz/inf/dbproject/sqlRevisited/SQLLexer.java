package ch.ethz.inf.dbproject.sqlRevisited;

import java.util.ArrayList;
import java.util.regex.*;

import ch.ethz.inf.dbproject.sqlRevisited.SQLToken.SQLTokenClass;

public class SQLLexer {

	/////
	//PUBLIC
	////
	
	/**
	 * Create a stream of tokens from a given SQL statement
	 * @param statement the SQL to analyze
	 * @return the stream of tokens, including error but without whitespace tokens
	 * 		   if an error occurred, the last element of the result will be an error token
	 */
	public ArrayList<SQLToken> tokenize(String statement) {
		ArrayList<SQLToken> tokenStream = new ArrayList<SQLToken>();
		
		Matcher matcher = pattern.matcher(statement);
		
		while (matcher.find()) {
			for (SQLTokenClass tokenClass : SQLToken.SQLTokenClass.values()) {
				String match = matcher.group(tokenClass.name());
				if (match != null && tokenClass != SQLToken.SQLTokenClass.WHITESPACE) {
					//"rewrite renaming of tables to always use explicit 'as'
					if (tokenStream.size() > 0 && tokenClass == SQLToken.SQLTokenClass.UNQUALIFIEDID && tokenStream.get(tokenStream.size()-1).tokenClass == SQLToken.SQLTokenClass.UNQUALIFIEDID) {
						tokenStream.add(new SQLToken(SQLToken.SQLTokenClass.AS, "as"));
					}
					tokenStream.add(new SQLToken(tokenClass, match));
					break;
				}
			}
		}
		
		return tokenStream;
	}
	
	//////
    //Implementation:
    //    use regular expressions to define lexical structure
    //    use util.regex package to find matches
	//    inspired by http://www.giocc.com/writing-a-lexer-in-java-1-7-using-regex-named-capturing-groups.html
	/////
	
	//Note: Pattern is thread safe so sharing it is fine
	private static final Pattern pattern = Pattern.compile(SQLToken.getPattern());;
}
