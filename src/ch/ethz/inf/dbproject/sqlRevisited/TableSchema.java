package ch.ethz.inf.dbproject.sqlRevisited;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import ch.ethz.inf.dbproject.Pair;

public class TableSchema {
	
	public final String tableName;
	private final String[] attributeNames;
	private final String[] qualifiers;
	private final SQLType[] attributeTypes;
	private final boolean[] isPrimaryKey;
	private List<TableSchemaAttributeDetail> attributes;
	
	////
	//CONSTRUCTORS
	////
	
	/**
	 * Create a schema of a table
	 * If the qualifier of an attribute is null, it will be set to the tableName, otherwise its qualifier is used.
	 * @param tableName the name of the table
	 * @param attributes
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
	
	/**
	 * Create a schema of a table
	 * If the qualifier of an attribute is null, it will be set to the tableName, otherwise its qualifier is used.
	 * @param tableName the name of the table
	 * @param attributes
	 */
	public TableSchema(String tableName, List<TableSchemaAttributeDetail> attributes) {
		int length = attributes.size();
		attributeNames = new String[length];
		attributeTypes = new SQLType[length];
		isPrimaryKey = new boolean[length];
		qualifiers = new String[length];
		this.attributes = Collections.unmodifiableList(attributes);
		this.tableName = tableName;
		initWithIterator(attributes);
	}
	
	/**
	 * Create a schema of a table
	 * The qualifier array is filled with the tableName - all attributes are qualified by the table name
	 * @param tableName
	 * @param attributeNames
	 * @param attributeTypes
	 * @param isPrimaryKey
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
	
	/**
	 * Create a schema of a table
	 * @param tableName
	 * @param attributeNames
	 * @param attributeTypes
	 * @param isPrimaryKey
	 * @param qualifiers
	 */
	public TableSchema(String tableName, String[] attributeNames, SQLType[] attributeTypes, boolean[] isPrimaryKey, String[] qualifiers) {
		assert (attributeNames.length == attributeTypes.length && attributeTypes.length == isPrimaryKey.length && isPrimaryKey.length == qualifiers.length);
		this.attributeNames = attributeNames;
		this.attributeTypes = attributeTypes;
		this.isPrimaryKey = isPrimaryKey;
		this.qualifiers = qualifiers;
		this.tableName = tableName;
	}
	
	////
	//ACCESS
	////
	
	/**
	 * Get the number of attributes of the table
	 * @return the number of attributes
	 */
	public int getLength()
	{
		return attributeNames.length;
	}
	
	/**
	 * Get the name of the table
	 * @return the name of the table
	 */
	public String getTableName(){
		return tableName;
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
	public String[] getAttributesNames(){
		return attributeNames.clone();
	}
	
	/**
	 * @return a list of all the attribute meta data, represented as TableSchemaAttributeDetail instances
	 */
	public List<TableSchemaAttributeDetail> getAttributes()
	{
		if (attributes == null) {
			attributes = new ArrayList<TableSchemaAttributeDetail>();
			for (int i=0; i<getLength(); i++) {
				attributes.add(new TableSchemaAttributeDetail(attributeNames[i], attributeTypes[i], isPrimaryKey[i], qualifiers[i]));
			}
			attributes = Collections.unmodifiableList(attributes);
		}
		return attributes;
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
	
	/**
	 * Test if the attribute at index i is primary key.
	 * @param i index to test
	 * @return true if i is in range and primary key, false otherwise
	 */
	public boolean isPrimaryKey(int i){
		return (i < isPrimaryKey.length) ? isPrimaryKey[i] : false;
	}

	/**
	 * @param name of the attribute (unqualified)
	 * @param startingFrom
	 * @return the smallest index k such that startingFrom<=k and attributeNames[k] equals name or -1 if no such index exists
	 */
	public int indexOfAttributeName(String name, int startingFrom) {
		
		while (startingFrom < attributeNames.length && !attributeNames[startingFrom].equals(name)) {
			startingFrom++;
		}
		
		if (startingFrom < attributeNames.length) {
			return startingFrom;
		} else {
			return -1;
		}
	}
	
	/**
	 * @param qualifier the table that qualifies the attribute
	 * @param attributeName the name of the attribute (unqualified)
	 * @return the smallest index k such that qualifier[k] equals qualifier and attribute[k] equals attributeName or -1 if no such index exists
	 */
	public int indexOfQualifiedAttributeName(String qualifier, String attributeName) {
		int cur = -1;
		do {
			cur = indexOfAttributeName(attributeName, cur+1);
		} while (cur >= 0 && !qualifiers[cur].equals(qualifier));
		
		return cur;
	}
	
	/**
	 * Test if tableSchema has attribute
	 */
	public boolean hasAttribute(Pair<String, String> attribute)
	{
		if (attribute == null) return false;
		return indexOf(attribute) >= 0;
	}
	
	/**
	 * @param identifier a qualified or unqualified identifier. If there are several attributes with the same identifier, searching for this attribute returns an unspecified result.
	 * @return the index of the attribute with the identifier, or -1 if no such index exists
	 */
	public int indexOf(Pair<String, String> identifier) {
		return indexOf(identifier.first, identifier.second);
	}
	
	/**
	 * Returns the index of an attribute.
	 * If qualifier is null or empty, use indexOfAttributeName.
	 * Otherwise use indexOfQualifiedAttributeName.
	 * @return the index of the attribute with the identifier, or -1 if no such index exists
	 */
	public int indexOf(String qualifier, String attributeName){
		assert(attributeName != null);
		
		if (qualifier == null || qualifier.length() == 0) {
			return indexOfAttributeName(attributeName, 0);
		} else {
			return indexOfQualifiedAttributeName(qualifier, attributeName);
		}
	}
	
	/**
	 * Get the size, from 0, of a particular number of attributes
	 * @param numberOfAttributes
	 * @return
	 */
	public int getSizeOfAttributes(int numberOfAttributes) {
		assert (numberOfAttributes <= getLength());
		int size = 0;
		for (int i = 0; i < numberOfAttributes; i++){
			size = size + this.attributeTypes[i].byteSizeOfType();
		}
		return size;
	}
	
	
	/**
	 * Get the size in bytes of each entry of the table
	 * @return the size of an entry in bytes
	 */
	public int getSizeOfEntry(){
		return getSizeOfAttributes(getLength());
	}
	
	/**
	 * Get the size in bytes of the keys of the table
	 * @return the size of keys in bytes
	 */
	public int getSizeOfKeys(){
		int size = 0; 
		for (int i = 0; i < this.attributeTypes.length; i++){
			if (this.isPrimaryKey[i])
				size = size + this.attributeTypes[i].byteSizeOfType();
		}
		return size;
	}
	
	/**
	 * Get all the keys of this TableSchema
	 * @return an array of types of keys (in order)
	 */
	public SQLType[] getKeys(){
		int i = 0;
		while (this.isPrimaryKey[i])
			i++;
		return Arrays.copyOf(this.attributeTypes.clone(), i);
		
	}

	////
	//FACTORY
	////
	
	/**
	 * @param schema
	 * @return a new schema that represents the concatenation of schema to this
	 */
	public TableSchema append(TableSchema schema) {
		return new TableSchema("", concatArrays(attributeNames, schema.attributeNames),
				concatArrays(attributeTypes, schema.attributeTypes), concatArrays(isPrimaryKey, schema.isPrimaryKey), concatArrays(qualifiers, schema.qualifiers));
	}
	
	/**
	 * @param newName
	 * @return a new schema that is equal to this, except its tableName is newName and its qualifiers is filled with newName
	 */
	public TableSchema renameSchema(String newName)
	{
		String[] newQualifiers = new String[getLength()];
		Arrays.fill(newQualifiers, newName);
		return new TableSchema(newName, attributeNames, attributeTypes, isPrimaryKey, newQualifiers);
	}
	
	////
	//OVERRIDING OBJECT
	////
	
	@Override
	public String toString()
	{
		return this.tableName + " " + this.getAttributes().toString();
	}
	
	@Override
	public boolean equals(Object other)
	{
		if (other != null && other.getClass().equals(TableSchema.class)) {
			return getAttributes().equals(((TableSchema)other).getAttributes());
			
		}
		return false;
	}
	
	////
	//PRIVATE
	////
	
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
}
