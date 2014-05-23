package ch.ethz.inf.dbproject.sqlRevisited;

import java.util.ArrayList;
import java.util.Arrays;

import ch.ethz.inf.dbproject.sqlRevisited.Parser.SQLToken;

public class ResultSet {

	private int cursor;
	private final TableSchema schema;
	private final ArrayList<byte[]> results;
	private Object[] currentTuple;
	
	/**
	 * A new ResultSet
	 */
	public ResultSet(TableSchema schema, ArrayList<byte[]> results) {
		this.schema = schema;
		this.results = results;
		cursor = -1;
		currentTuple = null;
	}

	/**
	 * Return the boolean stored at the column pointed by fieldname
	 * @param fieldname - the column name
	 * @return a java boolean
	 */
	public boolean getBoolean(String fieldname){
		return (boolean) getObject(fieldname);
	}
	
	/**
	 * Return the object stored at the column pointed by fieldname
	 * @param fieldname - the column name
	 * @return a java Object
	 */
	public Object getObject(String fieldname){
		//TODO handle dates (currently returns their string representation)
		return currentTuple[schema.indexOf(SQLToken.getFragmentsForIdentifier(fieldname.toLowerCase()))];
	}
	
	/**
	 * Return the String stored at the column pointed by index
	 * @param index - the column index
	 * @return a java String
	 */
	public String getString(int index){
		return (String) currentTuple[index];
	}
	
	/**
	 * Return the integer stored at the column pointed by index
	 * @param index - the column index
	 * @return a java integer
	 */
	public int getInt(int index){
		return (int) currentTuple[index];
	}
	
	public Object[] getObjects() {
		return Arrays.copyOf(currentTuple, currentTuple.length);
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean next() {
		cursor++;
		boolean hasNext = cursor < results.size();
		if (hasNext) {
			currentTuple = Serializer.getObjectsFromBytes(results.get(cursor), schema);
		}
		return hasNext;
	}

}
