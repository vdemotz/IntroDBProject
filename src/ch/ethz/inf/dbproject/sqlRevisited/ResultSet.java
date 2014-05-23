package ch.ethz.inf.dbproject.sqlRevisited;

import java.util.ArrayList;

public class ResultSet {

	private int cursor;
	private final TableSchema schema;
	private final ArrayList<byte[]> results;
	
	/**
	 * A new ResultSet
	 */
	public ResultSet(TableSchema schema, ArrayList<byte[]> results) {
		this.schema = schema;
		this.results = results;
		cursor = -1;
	}

	/**
	 * Return the boolean stored at the column pointed by fieldname
	 * @param fieldname - the column name
	 * @return a java boolean
	 */
	public boolean getBoolean(String fieldname){
		return false;
	}
	
	/**
	 * Return the object stored at the column pointed by fieldname
	 * @param fieldname - the column name
	 * @return a java Object
	 */
	public Object getObject(String fieldname){
		return null;
	}
	
	/**
	 * Return the String stored at the column pointed by index
	 * @param index - the column index
	 * @return a java String
	 */
	public String getString(int index){
		return null;
	}
	
	/**
	 * Return the integer stored at the column pointed by index
	 * @param index - the column index
	 * @return a java integer
	 */
	public int getInt(int index){
		return -1;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean next() {
		cursor++;
		return cursor < results.size();
	}

}
