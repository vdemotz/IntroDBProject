package ch.ethz.inf.dbproject.sqlRevisited;

import java.sql.Date;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;

import ch.ethz.inf.dbproject.sqlRevisited.Parser.SQLParseException;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.SQLParser;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.SQLToken;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.SQLTokenStream;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.SyntaxTreeDynamicNode;

public class InsertPreparedStatement  extends AbstractPreparedStatement {

	/**
	 * Create a new PrepareStatement insert
	 * @param insertTokensStream
	 */
	InsertPreparedStatement(ArrayList<SQLToken> insertTokensStream){
	}
}
