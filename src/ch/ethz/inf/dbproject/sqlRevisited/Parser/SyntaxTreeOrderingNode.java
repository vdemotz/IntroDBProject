package ch.ethz.inf.dbproject.sqlRevisited.Parser;

import ch.ethz.inf.dbproject.sqlRevisited.Parser.SQLToken.SQLTokenClass;

public class SyntaxTreeOrderingNode extends SyntaxTreeNode {

	public final boolean ascending;
	public final String identifier;
	
	public SyntaxTreeOrderingNode(SQLToken token, boolean asc) {
		assert(token.tokenClass == SQLToken.SQLTokenClass.UID || token.tokenClass == SQLToken.SQLTokenClass.QID);
		
		identifier = token.content;
		ascending = asc;
	}
	
}
