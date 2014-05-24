package ch.ethz.inf.dbproject.sqlRevisited;

import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.ParsedQuery;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.SQLLexer;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.SQLParser;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.SQLTokenStream;

public class Connection {
	
	private static Database db;
	private static Connection instance;
	private ReadWriteLock readWriteLock;
	private List<PhysicalTableInterface> listTablesConnections;
	
	/**
	 * Get a new connection
	 * @return a connection to database
	 * @throws SQLException 
	 */
	public static Connection getConnection() throws SQLException{
		synchronized(Connection.class) {
			if (instance == null){
				instance = new Connection();
			}
			return instance;
		}
	}
	
	/**
	 * Create a new connection to the database.
	 */
	private Connection() throws SQLException {
		if (db == null) {
			try{
				db = new Database();
			} catch (Exception ex){
				System.err.println("Failed to open new database");
				throw new SQLException();
			}
		}
		readWriteLock = new ReentrantReadWriteLock();
		try {
			listTablesConnections = db.getAllTablesConnections();
		} catch (Exception e) {
			System.err.println("Failed to get all tables connections");
			throw new SQLException();
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
			return new DeletePreparedStatement(pq, (WriteLock)readWriteLock.writeLock(), listTablesConnections);
		} else if (pq.typeParsedQuery == ParsedQuery.TypeParsedQuery.INSERT){
			return new InsertPreparedStatement(pq, (WriteLock)readWriteLock.writeLock(), listTablesConnections);
		} else if (pq.typeParsedQuery == ParsedQuery.TypeParsedQuery.UPDATE){
			return new UpdatePreparedStatement(pq, (WriteLock)readWriteLock.writeLock(), listTablesConnections);
		} else if (pq.typeParsedQuery == ParsedQuery.TypeParsedQuery.SELECT){
			return new SelectPreparedStatement(pq, (ReadLock)readWriteLock.readLock(), listTablesConnections);
		} else {
			throw new SQLException();
		}
	}
	
}
