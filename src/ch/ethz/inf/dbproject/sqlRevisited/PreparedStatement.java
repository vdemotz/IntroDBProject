package ch.ethz.inf.dbproject.sqlRevisited;

import java.util.Date;

public interface PreparedStatement {
	
	/**
	 * Execute a search in DB and return the generated ResultSet
	 * @return The generated ResultSet
	 * @throws SQLException 
	 */
	public ResultSet executeQuery() throws SQLException;
	
	/**
	 * Execute any kind of sql query of this PreparedStatement
	 * @throws SQLException 
	 */
	public boolean execute() throws SQLException;
	
	/**
	 * Retrieves the current result as a ResultSet object. This method should be called only once per result.
	 * @return a ResultSet
	 * @throws SQLException 
	 */
	public ResultSet getResultSet() throws SQLException;

	/**
	 * Sets the designated parameter to the given Java int value.
	 * @param index - the first parameter is 1, the second is 2, ...
	 * @param value - the parameter value
	 * @throws SQLException 
	 */
	public void setInt(int index, int value) throws SQLException;
	
	/**
	 * Sets the designated parameter to the given Java String value.
	 * @param index - the first parameter is 1, the second is 2, ...
	 * @param value - the parameter value
	 * @throws SQLException 
	 */
	public void setString(int index, String value) throws SQLException;
	
	/**
	 * Sets the designated parameter to the given java.sql Date
	 * @param index - the first parameter is 1, the second is 2, ...
	 * @param value - the parameter value
	 * @throws SQLException 
	 */
	public void setDate(int index, java.util.Date value) throws SQLException;
	
	/**
	 * Sets the designated parameter to the given java.sql TimeStamp
	 * @param index - the first parameter is 1, the second is 2, ...
	 * @param value - the parameter value
	 * @throws SQLException 
	 */
	public void setTimestamp(int index, java.util.Date value) throws SQLException;
	
	/**
	 * Sets the designated parameter to sql NULL
	 * @param index - the first parameter is 1, the second is 2, ...
	 * @param value - the parameter value
	 * @throws SQLException 
	 */
	public void setNull(int index, java.sql.Types value) throws SQLException;
	
	/**
	 * Sets the designated parameter to the given Java Object
	 * @param index - the first parameter is 1, the second is 2, ...
	 * @param value - the parameter value
	 * @throws SQLException 
	 */
	public void setObject(int index, Object value) throws SQLException;
	
	/**
	 * Sets the designated parameter to the given Java boolean
	 * @param index - the first parameter is 1, the second is 2, ...
	 * @param value - the parameter value
	 * @throws SQLException 
	 */
	public void setBoolean(int index, boolean value) throws SQLException;
	
	/**
	 * Return the number of tuples changed by last execute call
	 * @return
	 */
	public int getUpdateCount();

}
