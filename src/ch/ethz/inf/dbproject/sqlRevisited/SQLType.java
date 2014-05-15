package ch.ethz.inf.dbproject.sqlRevisited;

public class SQLType {
	
	//Number of characters of Date and Datetime
	public int CHAR_DATE = 10;
	public int CHAR_DATETIME = 19;
	
	
	public enum BaseType {
		Integer,
		Char,
		Varchar,
		Date,
		Datetime,
		Boolean
	}
	
	public final BaseType type;
	public final Integer size;
	
	/**
	 * instantiates a type with no size parameter (size will be null)
	 * @param basetype
	 */
	public SQLType(BaseType basetype) {
		assert(basetype != BaseType.Varchar && basetype != BaseType.Char);
		
		this.type = basetype;
		this.size = null;
	}
	
	/**
	 * @param basetype
	 * @param size if the type has a variable size parameter, it is specified in size
	 */
	public SQLType(BaseType basetype, Integer size) {
		assert (size == null || basetype == BaseType.Varchar ||  basetype == BaseType.Char);
		assert(size != null || (basetype != BaseType.Varchar && basetype != BaseType.Char));
		
		this.type = basetype;
		this.size = size;
	}
	
	/**
	 * Compute the size in bytes needed to store this particular type
	 * @return size in bytes, -1 if problem occured
	 */
	public int byteSizeOfType(){
		if(this.type == BaseType.Integer){
			return (Integer.SIZE);
		} else if (this.type == BaseType.Boolean){
			return (Character.SIZE);
		} else if (this.type == BaseType.Char){
			return (Character.SIZE);
		} else if (this.type == BaseType.Varchar){
			return (Character.SIZE*this.size);
		} else if (this.type == BaseType.Date){
			return (Character.SIZE*CHAR_DATE);
		} else if (this.type == BaseType.Datetime){
			return (Character.SIZE*CHAR_DATETIME);
		} 
		return -1;
	}
	
	////
	//OVERRIDING OBJECT
	////
	
	@Override
	public boolean equals(Object other)
	{
		boolean result = false;
		if (other != null && other.getClass().equals(SQLType.class)) {
			SQLType otherType = (SQLType)other;
			result = otherType.type == type;
			if (size == null) {
				result = result && otherType.size == null;
			} else {
				result = result && size.equals(otherType.size);
			}
		}
		return result;
	}
	
	@Override
	public String toString()
	{
		String result =  this.type.toString();
		if (size != null) result = result + "(" + size + ")";
		return result;
	}
}

