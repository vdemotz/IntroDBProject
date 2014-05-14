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
	 * Get the name of the table
	 * @return the name of the table
	 */
	public String getTableName(){
		return tableName;
	}
	
	/**
	 * Get a copy of the attributes names of the table
	 * @return all attributes names of the table
	 */
	public String[] getAttributesNames(){
		return attributeNames.clone();
	}
	
	/**
	 * Get a copy of the attributes types of the table
	 * @return all attributes types of the table
	 */
	public SQLType[] getAttributesTypes(){
		return attributeTypes.clone();
	}
	
	/**
	 * Get a copy of the boolean which indicates if attribute is a key
	 * @return 
	 * @return all boolean which indicates if attribute is a key
	 */
	public boolean[] getIfPrimaryKey(){
		return isPrimaryKey.clone();
	}
	
}
