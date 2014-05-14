package ch.ethz.inf.dbproject.sqlRevisited;

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
		for (int i=0; i<qualifiers.length; i++) {
			qualifiers[i] = baseSchema.tableName;
		}
		this.qualifiers = qualifiers;
	}
	
}
