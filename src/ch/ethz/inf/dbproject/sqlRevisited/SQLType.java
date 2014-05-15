package ch.ethz.inf.dbproject.sqlRevisited;

public class SQLType {
	
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
	 * @return size in bytes
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
			return -1;
		} else if (this.type == BaseType.Datetime){
			return -1;
		} 
		return -1;
	}
	
	public String toString()
	{
		String result =  this.type.toString();
		if (size != null) result = result + "(" + size + ")";
		return result;
	}
}

