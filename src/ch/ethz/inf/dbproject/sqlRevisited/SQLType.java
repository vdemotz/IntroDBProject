package ch.ethz.inf.dbproject.sqlRevisited;

public class SQLType {
	
	//Number of characters of Date and Datetime
	public static final int CHAR_DATE = 10;
	public static final int CHAR_DATETIME = 19;
	public static final int BYTE_SIZE = 8;
	public static final int CHARACTER_BYTE_SIZE = 1;
	public static final int INT_BYTE_SIZE = 4;
	
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
			return (INT_BYTE_SIZE);
		} else if (this.type == BaseType.Boolean){
			return (CHARACTER_BYTE_SIZE);
		} else if (this.type == BaseType.Char){
			return (CHARACTER_BYTE_SIZE);
		} else if (this.type == BaseType.Varchar){
			return (CHARACTER_BYTE_SIZE*this.size+INT_BYTE_SIZE);
		} else if (this.type == BaseType.Date){
			return (CHARACTER_BYTE_SIZE*CHAR_DATE+INT_BYTE_SIZE);
		} else if (this.type == BaseType.Datetime){
			return (CHARACTER_BYTE_SIZE*CHAR_DATETIME+INT_BYTE_SIZE);
		} 
		return -1;
	}
	
	////
	//STATIC CONTANT TYPES
	////
	
	public static final SQLType INTEGER = new SQLType(SQLType.BaseType.Integer);
	public static final SQLType DATE = new SQLType(SQLType.BaseType.Varchar, CHAR_DATE);
	public static final SQLType DATETIME = new SQLType(SQLType.BaseType.Varchar, CHAR_DATETIME);
	public static final SQLType BOOLEAN = new SQLType(SQLType.BaseType.Boolean);
	
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

