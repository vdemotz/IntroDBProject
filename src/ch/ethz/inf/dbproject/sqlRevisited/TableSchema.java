package ch.ethz.inf.dbproject.sqlRevisited;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import ch.ethz.inf.dbproject.Pair;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.SQLSemanticException;

public class TableSchema {
	
	public final String tableName;
	public final String[] attributeNames;
	public final String[] qualifiers;
	public final SQLType[] attributeTypes;
	public final boolean[] isPrimaryKey;
	
	/**
	 * Get a schema of a table in DB
	 * @param tableName the name of the table
	 */
	public TableSchema(String tableName, TableSchemaAttributeDetail[] attributes) {
		
		int length = attributes.length;
		attributeNames = new String[length];
		attributeTypes = new SQLType[length];
		isPrimaryKey = new boolean[length];
		qualifiers = new String[length];
		this.tableName = tableName;
		initWithIterator(Arrays.asList(attributes));
	}
	
	public TableSchema(String tableName, List<TableSchemaAttributeDetail> attributes) {
		
		int length = attributes.size();
		attributeNames = new String[length];
		attributeTypes = new SQLType[length];
		isPrimaryKey = new boolean[length];
		qualifiers = new String[length];
		this.tableName = tableName;
		initWithIterator(attributes);
	}
	
	private void initWithIterator(Iterable<TableSchemaAttributeDetail> attributes) {
		int i=0;
		for (TableSchemaAttributeDetail attribute : attributes) {
			attributeNames[i] = attribute.attributeName;
			attributeTypes[i] = attribute.attributeType;
			isPrimaryKey[i] = attribute.isKey;
			if (attribute.qualifier != null) {
				qualifiers[i] = attribute.qualifier;
			} else {
				qualifiers[i] = tableName;
			}
			i++;
		}
	}
	
	/**
	 * Get a schema of a table in DB
	 * @param tableName the name of the table
	 */
	public TableSchema(String tableName, String[] attributeNames, SQLType[] attributeTypes, boolean[] isPrimaryKey) {
		assert (attributeNames.length == attributeTypes.length && attributeTypes.length == isPrimaryKey.length);
		this.attributeNames = attributeNames;
		this.attributeTypes = attributeTypes;
		this.isPrimaryKey = isPrimaryKey;
		this.tableName = tableName;
		qualifiers = new String[attributeNames.length];
		Arrays.fill(qualifiers, tableName);
	}
	
	public TableSchema(String tableName, String[] attributeNames, SQLType[] attributeTypes, boolean[] isPrimaryKey, String[] qualifiers) {
		this.attributeNames = attributeNames;
		this.attributeTypes = attributeTypes;
		this.isPrimaryKey = isPrimaryKey;
		this.qualifiers = qualifiers;
		this.tableName = tableName;
	}
	
	public int getLength()
	{
		return attributeNames.length;
	}
	
	/**
	 * Get a copy of the attributes names of the table as a list
	 * @return all attributes names of the table
	 */
	public List<String> getAttributesNames(){
		return Arrays.asList(attributeNames.clone());
	}
	
	/**
	 * Get a copy of the attributes names of the table as a list
	 * @return all attributes names of the table
	 */
	public List<String> getQualifiers(){
		return Arrays.asList(qualifiers.clone());
	}
	
	/**
	 * Get a copy of the attributes types of the table as a list
	 * @return all attributes types of the table
	 */
	public List<SQLType> getAttributesTypes(){
		return Arrays.asList(attributeTypes.clone());
	}
	
	public List<TableSchemaAttributeDetail> getAttributes()
	{
		List<TableSchemaAttributeDetail> attributes = new ArrayList<TableSchemaAttributeDetail>();
		for (int i=0; i<getLength(); i++) {
			attributes.add(new TableSchemaAttributeDetail(attributeNames[i], attributeTypes[i], isPrimaryKey[i], qualifiers[i]));
		}
		return attributes;
	}
	
	/**
	 * Get a copy of the the names of the primary keys of the table as a list
	 * @return all primary keys names of the table
	 */
	public List<String> getPrimaryKeyNames(){
		return null;
	}
	
	public int indexOfAttributeName(String name, int startingFrom) throws SQLSemanticException {
		try {
			while (!attributeNames[startingFrom].equals(name)) {
				startingFrom++;
			}
		} catch (IndexOutOfBoundsException e) {
			throw new SQLSemanticException(SQLSemanticException.Type.NoSuchAttributeException, name);
		}
		assert(attributeNames[startingFrom].equals(name));
		return startingFrom;
	}
	
	public int indexOfQualifiedAttributeName(String qualifier, String attributeName) throws SQLSemanticException {
		int cur = -1;
		do {
			try {
				cur = indexOfAttributeName(attributeName, cur+1);
			} catch (SQLSemanticException e) {
				System.out.println(this);
				throw new SQLSemanticException(SQLSemanticException.Type.NoSuchAttributeException, qualifier + "." + attributeName);
			}
		} while (!qualifiers[cur].equals(qualifier));
		return cur;
	}

	public TableSchema append(TableSchema schema) {
		return new TableSchema(tableName + "||" + schema.tableName, concatArrays(attributeNames, schema.attributeNames),
				concatArrays(attributeTypes, schema.attributeTypes), concatArrays(isPrimaryKey, schema.isPrimaryKey), concatArrays(qualifiers, schema.qualifiers));
	}
	
	public TableSchema renameSchema(String newName)
	{
		String[] newQualifiers = new String[getLength()];
		Arrays.fill(newQualifiers, newName);
		return new TableSchema(newName, attributeNames, attributeTypes, isPrimaryKey, newQualifiers);
	}
	
	private static <T> T[] concatArrays(T[] left, T[] right)
	{
		T[] result = Arrays.copyOf(left, left.length+right.length);
		for (int i=left.length; i < left.length+right.length; i++) {
			result[i] = right[i-left.length];
		}
		return result;
	}
	
	private static boolean[] concatArrays(boolean[] left, boolean[] right)
	{
		boolean[] result = Arrays.copyOf(left, left.length+right.length);
		for (int i=left.length; i < left.length+right.length; i++) {
			result[i] = right[i-left.length];
		}
		return result;
	}
	
	public String toString()
	{
		return this.tableName + " " + this.getAttributes().toString();
	}
}
