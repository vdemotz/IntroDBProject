package ch.ethz.inf.dbproject.sqlRevisited;

public class TableSchemaAttributeDetail {

	final String attributeName;
	final SQLType attributeType;
	final boolean isKey;
		
	/**
	 * Create (get from DB) a new AttributeDetail
	 */
	public TableSchemaAttributeDetail(String attributeName, SQLType attributeType, boolean isKey) {
		this.attributeName = attributeName;
		this.attributeType = attributeType;
		this.isKey = isKey;
	}
	
}
