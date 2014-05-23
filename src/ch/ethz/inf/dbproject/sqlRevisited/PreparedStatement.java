package ch.ethz.inf.dbproject.sqlRevisited;

public interface PreparedStatement {
	
	/**
	 * Execute a search in DB and return the generated ResultSet
	 * @return The generated ResultSet
	 */
	public ResultSet executeQuery();
	
	/**
	 * Execute any kind of sql query of this PreparedStatement
	 */
	public boolean execute();
	
	/**
	 * Retrieves the current result as a ResultSet object. This method should be called only once per result.
	 * @return a ResultSet
	 */
	public ResultSet getResultSet();

	/**
	 * Sets the designated parameter to the given Java int value.
	 * @param index - the first parameter is 1, the second is 2, ...
	 * @param value - the parameter value
	 */
	public void setInt(int index, int value);
	
	/**
	 * Sets the designated parameter to the given Java String value.
	 * @param index - the first parameter is 1, the second is 2, ...
	 * @param value - the parameter value
	 */
	public void setString(int index, String value);
	
	/**
	 * Sets the designated parameter to the given java.sql Date
	 * @param index - the first parameter is 1, the second is 2, ...
	 * @param value - the parameter value
	 */
	public void setDate(int index, java.sql.Date value);
	
	/**
	 * Sets the designated parameter to the given java.sql TimeStamp
	 * @param index - the first parameter is 1, the second is 2, ...
	 * @param value - the parameter value
	 */
	public void setTimeStamp(int index, java.sql.Timestamp value);
	
	/**
	 * Sets the designated parameter to sql NULL
	 * @param index - the first parameter is 1, the second is 2, ...
	 * @param value - the parameter value
	 */
	public void setNull(int index, java.sql.Types value);
	
	/**
	 * Sets the designated parameter to the given Java Object
	 * @param index - the first parameter is 1, the second is 2, ...
	 * @param value - the parameter value
	 */
	public void setObject(int index, Object value);
	
	/**
	 * Sets the designated parameter to the given Java boolean
	 * @param index - the first parameter is 1, the second is 2, ...
	 * @param value - the parameter value
	 */
	public void setBoolean(int index, boolean value);
	
	/**
	 * Return the number of tuples changed by last execute call
	 * @return
	 */
	public int getUpdateCount();
}
