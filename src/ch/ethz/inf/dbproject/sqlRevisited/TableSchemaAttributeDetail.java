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
		assert(attributeName != null);
		assert(attributeType != null);
		this.qualifier = null;
		this.attributeName = attributeName;
		this.attributeType = attributeType;
		this.isKey = isKey;
	}
	
	public TableSchemaAttributeDetail(String attributeName, SQLType attributeType, boolean isKey, String qualifier) {
		assert(attributeName != null);
		assert(attributeType != null);
		this.qualifier = qualifier;
		this.attributeName = attributeName;
		this.attributeType = attributeType;
		this.isKey = isKey;
	}
	
	////
	//OVERRIDING OBJECT
	////
	
	@Override
	public boolean equals(Object other) {
		boolean result = false;
		if (other != null && other.getClass().equals(TableSchemaAttributeDetail.class)) {
			TableSchemaAttributeDetail otherAttribute = (TableSchemaAttributeDetail)other;
			result = isKey == otherAttribute.isKey && attributeName.equals(otherAttribute.attributeName) && attributeType.equals(otherAttribute.attributeType);
			if (qualifier == null) {
				result = result && qualifier == null;
			} else {
				result = result && qualifier.equals(otherAttribute.qualifier);
			}
		}
		return result;
	}
	
	@Override
	public String toString()
	{
		return qualifier + "." + attributeName + "::" + attributeType;
	}
}
