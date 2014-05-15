package ch.ethz.inf.dbproject.sqlRevisited;

public class Connection {
	
	/**
	 * Create a new connection to the database.
	 */
	public Connection() {
		
	}
	
	/**
	 * Prepare a prepared statement from a string which represents a sql query.
	 * @param stringQuery : a sql query
	 * @return a prepared statement to be set and executed.
	 */
	public PreparedStatement prepareStatement(String stringQuery){
		return null;
	}
	
	/**
	 * Return the first object of the given table name
	 * @param tableName a table name of the database
	 * @return the first Object of this table
	 */
	public Object min(String tableName){
		return null;
	}
	
	/**
	 * Get a particular object of a table given a primary key
	 * @param primaryKeys which identify the object
	 * @param tableName a table name of the database
	 * @return the object with given primary keys
	 */
	public Object get(Object[] primaryKeys, String tableName){
		return null;
	}
	
	/**
	 * Get the successor of a particular object of a table given a primary key
	 * @param primaryKeys which identify the predecessor of returned object
	 * @param tableName a table name of the database
	 * @return the successor of the object with given primary keys
	 */
	public Object succ(Object[] primaryKeys, String tableName){
		return null;
	}
	
	/**
	 * Delete an object from a table if exists given a primary key
	 * @param primaryKeys which identify the predecessor of returned object
	 * @param tableName a table name of the database
	 * @return true if object has been deleted, else false
	 */
	public boolean delete(Object[] primaryKeys, String tableName){
		return false;
	}

	/**
	 * Insert an object into a table
	 * @param toInsert object to be inserted
	 * @param tableName a table name of the database
	 * @return true if object has been inserted, else false
	 */
	public boolean insert(Object toInsert, String tableName){
		return false;
	}
	
	/**
	 * Update an object of a table
	 * @param primaryKeys which identify the object to update
	 * @param toUpdate object to be updated 
	 * @param tableName a table name of the database
	 * @return true if object has been updated, else false
	 */
	public boolean update(Object[] primaryKeys, Object toUpdate, String tableName){
		return false;
	}
}
