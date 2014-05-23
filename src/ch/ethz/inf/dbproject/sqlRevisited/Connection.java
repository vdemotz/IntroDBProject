package ch.ethz.inf.dbproject.sqlRevisited;

import java.util.ArrayList;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.SQLLexer;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.SQLToken;

public class Connection {
	
	private static Database db;
	private static Connection instance;
	
	/**
	 * Get a new connection
	 * @return a connection to database
	 */
	public static Connection getConnection(){
		if (instance == null){
			instance = new Connection();
		}
		return instance;
	}
	
	/**
	 * Create a new connection to the database.
	 */
	private Connection() {
		if (db == null)
			try{
				db = new Database();
			} catch (Exception ex){
				ex.printStackTrace();
				System.err.println("Failed to create new Database");
			}
	}
	
	/**
	 * Prepare a prepared statement from a string which represents a sql query.
	 * @param stringQuery : a sql query
	 * @return a prepared statement to be set and executed.
	 */
	public PreparedStatement prepareStatement(String stringQuery) throws SQLException{
		ArrayList<SQLToken> tokensStream = new SQLLexer().tokenize(stringQuery);
		SQLToken type = tokensStream.get(0);
		
		if (type.tokenClass == SQLToken.SQLTokenClass.DELETE){
			return new DeletePreparedStatement(tokensStream);
		} else if (type.tokenClass == SQLToken.SQLTokenClass.INSERTINTO){
			return new InsertPreparedStatement(tokensStream);
		} else if (type.tokenClass == SQLToken.SQLTokenClass.UPDATE){
			return new UpdatePreparedStatement(tokensStream);
		} else if (type.tokenClass == SQLToken.SQLTokenClass.SELECT){
			return new SelectPreparedStatement(tokensStream);
		} else {
			throw new SQLException();
		}
	}
}
