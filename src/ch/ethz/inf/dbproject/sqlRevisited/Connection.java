package ch.ethz.inf.dbproject.sqlRevisited;

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
	public PreparedStatement prepareStatement(String stringQuery){
		return null;
	}
	
	/**
	 * Return the first object of the given table name
	 * @param tableName a table name of the database
	 * @return the first Object of this table
	 */
	public Object min(String tableName) throws Exception{
		return db.min(tableName);
	}
	
	/**
	 * Get a particular object of a table given a primary key
	 * @param primaryKeys which identify the object
	 * @param tableName a table name of the database
	 * @return the object with given primary keys
	 */
	public Object get(Object[] primaryKeys, String tableName){
		return db.get(primaryKeys, tableName);
	}
	
	/**
	 * Get the successor of a particular object of a table given a primary key
	 * @param primaryKeys which identify the predecessor of returned object
	 * @param tableName a table name of the database
	 * @return the successor of the object with given primary keys
	 */
	public Object succ(Object[] primaryKeys, String tableName){
		return db.succ(primaryKeys, tableName);
	}
	
	/**
	 * Delete an object from a table if exists given a primary key
	 * @param primaryKeys which identify the predecessor of returned object
	 * @param tableName a table name of the database
	 * @return true if object has been deleted, else false
	 */
	public boolean delete(Object[] primaryKeys, String tableName){
		return db.delete(primaryKeys, tableName);
	}

	/**
	 * Insert an object into a table
	 * @param toInsert object to be inserted
	 * @param tableName a table name of the database
	 * @return true if object has been inserted, else false
	 */
	public boolean insert(Object toInsert, String tableName){
		return db.insert(toInsert, tableName);
	}
	
	/**
	 * Update an object of a table
	 * @param primaryKeys which identify the object to update
	 * @param toUpdate object to be updated 
	 * @param tableName a table name of the database
	 * @return true if object has been updated, else false
	 */
	public boolean update(Object[] primaryKeys, Object toUpdate, String tableName){
		return db.update(primaryKeys, toUpdate, tableName);
	}
	
	/**
	 * Get a table schema for a given table
	 * @param tableName the table name
	 * @return a table schema representing the schema of the table
	 * @throws Exception 
	 */
	public TableSchema getTableSchema(String tableName) throws Exception{
		return db.getTableSchema(tableName);
	}
}
