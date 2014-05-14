package ch.ethz.inf.dbproject.sqlRevisited;

import java.util.ArrayList;
import java.util.Arrays;

public class AttributedTableSchema extends TableSchema {

	//qualifiers specify the qualifier that can be used to access a 	
	public final String[] qualifiers;
	
	public AttributedTableSchema(String tableName, String[] attributeNames, SQLType[] attributeTypes, boolean[] isPrimaryKey, String[] qualifiers) {
		super(tableName, attributeNames, attributeTypes, isPrimaryKey);
		this.qualifiers = qualifiers;
	}

	/**
	 * instantiates an attributed table schema where all attributes are qualified by the base table name
	 * @param baseSchema
	 */
	public AttributedTableSchema(TableSchema baseSchema) {
		super(baseSchema.tableName, baseSchema.attributeNames, baseSchema.attributeTypes, baseSchema.isPrimaryKey);
		String[] qualifiers = new String[baseSchema.attributeNames.length];
		Arrays.fill(qualifiers, baseSchema.tableName);
		this.qualifiers = qualifiers;
	}
	
	public AttributedTableSchema append(AttributedTableSchema schema) {
		return new AttributedTableSchema(tableName, concatArrays(attributeNames, schema.attributeNames),
				concatArrays(attributeTypes, schema.attributeTypes), concatArrays(isPrimaryKey, schema.isPrimaryKey), concatArrays(qualifiers, schema.qualifiers));
	}
	
	public AttributedTableSchema renameSchema(String newName)
	{
		String[] newQualifiers = new String[getLength()];
		Arrays.fill(newQualifiers, newName);
		return new AttributedTableSchema(tableName, attributeNames, attributeTypes, isPrimaryKey, newQualifiers);
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
