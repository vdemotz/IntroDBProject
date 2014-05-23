package ch.ethz.inf.dbproject.sqlRevisited;

import ch.ethz.inf.dbproject.sqlRevisited.Parser.ParsedQuery;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.SQLLexer;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.SQLParser;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.SQLTokenStream;

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
		//Tokenize
		SQLTokenStream sqlTokenStream = new SQLTokenStream(new SQLLexer().tokenize(stringQuery));
		//Parse
		ParsedQuery pq = new SQLParser().parse(sqlTokenStream);
		
		if (pq.typeParsedQuery == ParsedQuery.TypeParsedQuery.DELETE){
			return new DeletePreparedStatement(pq);
		} else if (pq.typeParsedQuery == ParsedQuery.TypeParsedQuery.INSERT){
			return new InsertPreparedStatement(pq);
		} else if (pq.typeParsedQuery == ParsedQuery.TypeParsedQuery.UPDATE){
			return new UpdatePreparedStatement(pq);
		} else if (pq.typeParsedQuery == ParsedQuery.TypeParsedQuery.SELECT){
			return new SelectPreparedStatement(pq);
		} else {
			throw new SQLException();
		}
	}
}
