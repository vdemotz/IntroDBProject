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
	
	private Database db;
	private static Connection instance;
	private ReadWriteLock readWriteLock;
	private List<PhysicalTableInterface> listTablesConnections;
	
	/**
	 * Get the connection to the database at the default relative location.
	 * @return the connection to the default database
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
	 * Between different connection instances, there are no synchronization guarantees.
	 */
	private Connection() throws SQLException {
		init("");
	}
	
	/**
	 * Create a new connection to the database.
	 * Between different connection instances, there are no synchronization guarantees.
	 * This means that you should never create two Connection instances with the same databaseDirectory
	 */
	public Connection(String databaseDirectory) throws SQLException {
		init(databaseDirectory);
	}
	
	private void init(String databaseDirectory) throws SQLPhysicalException {
		if (db == null) {
			try {
				db = new Database(databaseDirectory);
			} catch (Exception ex){
				ex.printStackTrace();
				throw new SQLPhysicalException();
			}
		}
		readWriteLock = new ReentrantReadWriteLock();
		try {
			listTablesConnections = db.getAllTablesConnections();
		} catch (Exception e) {
			e.printStackTrace();
			throw new SQLPhysicalException();
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
		//DEMONSTRATION :: System.out.println(sqlTokenStream);
		//Parse
		ParsedQuery pq = new SQLParser().parse(sqlTokenStream);
		
		if (pq.typeParsedQuery == ParsedQuery.TypeParsedQuery.DELETE){
			return new DeletePreparedStatement(pq, readWriteLock.writeLock(), listTablesConnections);
		} else if (pq.typeParsedQuery == ParsedQuery.TypeParsedQuery.INSERT){
			return new InsertPreparedStatement(pq, readWriteLock.writeLock(), listTablesConnections);
		} else if (pq.typeParsedQuery == ParsedQuery.TypeParsedQuery.UPDATE){
			return new UpdatePreparedStatement(pq, readWriteLock.writeLock(), listTablesConnections);
		} else if (pq.typeParsedQuery == ParsedQuery.TypeParsedQuery.SELECT){
			return new SelectPreparedStatement(pq, readWriteLock.readLock(), listTablesConnections);
		} else {
			throw new SQLException();
		}
	}
	
}
