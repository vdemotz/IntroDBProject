package ch.ethz.inf.dbproject.sqlRevisited;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.inf.dbproject.Pair;

public class TableSchema {

	
	/*
	 * This class is meant to be accessed through the Connection, to make it synchronized.
	 * A possibility is to make it as a subclass of Connection.
	 * 
	 * To be added : The array of keys
	 * 
	 * To see if needed : only access on names and attributes. (Instead of directly with pair<Name, Type>
	 */
	
	ArrayList<AttributeDetail> attributes;
	
	
	/**
	 * Get a schema of a table in DB
	 * @param tableName the name of the table
	 */
	public TableSchema(String tableName){

	}
	
	/**
	 * Get a List of the attributes names of the table
	 * @return all attributes names of the table
	 */
	public List<String> getAttributesNames(){
		return null;
	}
	
	/**
	 * Get a List of the attributes types of the table
	 * @return all attributes types of the table
	 */
	public List<SQLType> getAttributesTypes(){
		return null;
	}
	
	/**
	 * Get a List of attributes
	 * @return A pair containing on first the name of the attribute and on second the type of the attribute
	 */
	public List<Pair<String, SQLType>> getAttributesNamesAndTypes(){
		return null;
	}
	
	/**
	 * Get a List of the names of the primary keys of the table
	 * @return all primary keys names of the table
	 */
	public List<String> getPrimaryKeysNames(){
		return null;
	}
	

	
	protected class AttributeDetail{
		String attributeName;
		SQLType attributeType;
		boolean isKey;
		int size; //in case of VARCHAR, -1 (or 0) otherwise
		//int index;
		
		/**
		 * Create (get from DB) a new AttributeDetail
		 */
		public AttributeDetail() {
			
		}
	}
}
