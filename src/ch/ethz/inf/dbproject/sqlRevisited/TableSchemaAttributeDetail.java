package ch.ethz.inf.dbproject.sqlRevisited;

public class TableSchemaAttributeDetail {

	public final String qualifier;
	public final String attributeName;
	public final SQLType attributeType;
	public final boolean isKey;
		
	/**
	 * Create (get from DB) a new AttributeDetail
	 */
	public TableSchemaAttributeDetail(String attributeName, SQLType attributeType, boolean isKey) {
		this.qualifier = null;
		this.attributeName = attributeName;
		this.attributeType = attributeType;
		this.isKey = isKey;
	}
	
	public TableSchemaAttributeDetail(String attributeName, SQLType attributeType, boolean isKey, String qualifier) {
		this.qualifier = qualifier;
		this.attributeName = attributeName;
		this.attributeType = attributeType;
		this.isKey = isKey;
	}
	
	public String toString()
	{
		return qualifier + "." + attributeName + "::" + attributeType;
	}
}
