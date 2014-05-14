package ch.ethz.inf.dbproject.sqlRevisited;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.ethz.inf.dbproject.Pair;

public class TableSchema {
	
	public final String tableName;
	public final String[] attributeNames;
	public final SQLType[] attributeTypes;
	public final boolean[] isPrimaryKey;
	
	/**
	 * Get a schema of a table in DB
	 * @param tableName the name of the table
	 */
	public TableSchema(String tableName, String[] attributeNames, SQLType[] attributeTypes, boolean[] isPrimaryKey) {
		assert (attributeNames.length == attributeTypes.length && attributeTypes.length == isPrimaryKey.length);
		this.tableName = tableName;
		this.attributeNames = attributeNames;
		this.attributeTypes = attributeTypes;
		this.isPrimaryKey = isPrimaryKey;
	}
	
	/**
	 * Get a copy of the attributes names of the table as a list
	 * @return all attributes names of the table
	 */
	public List<String> getAttributesNames(){
		return Arrays.asList(attributeNames.clone());
	}
	
	/**
	 * Get a copy of the attributes types of the table as a list
	 * @return all attributes types of the table
	 */
	public List<SQLType> getAttributesTypes(){
		return Arrays.asList(attributeTypes.clone());
	}
	
	/**
	 * Get a copy of the the names of the primary keys of the table as a list
	 * @return all primary keys names of the table
	 */
	public List<String> getPrimaryKeyNames(){
		return null;
	}
	
	protected class AttributeDetail{
		String attributeName;
		SQLType attributeType;
		boolean isKey;
		
		/**
		 * Create (get from DB) a new AttributeDetail
		 */
		public AttributeDetail() {
			
		}
	}
}
