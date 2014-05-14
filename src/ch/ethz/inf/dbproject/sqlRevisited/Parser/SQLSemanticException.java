package ch.ethz.inf.dbproject.sqlRevisited.Parser;

public class SQLSemanticException extends Exception {

	public enum Type
	{
		NoSuchTableException("No table with name : "),
		InternalError("An internal error occurred : ");
		
		public final String message;
		Type(String message) {
			this.message = message;
		}
	}
	
	public final Type type;
	public final String errorAttribute;
	
	SQLSemanticException(Type type) {
		errorAttribute = "";
		this.type = type;
	}
	
	SQLSemanticException(Type type, String errorAttribute) {
		this.errorAttribute = errorAttribute;
		this.type = type;
	}
	
	public String toString()
	{
		return "SQL Semantic Error occured: " + type.message + errorAttribute;
	}
}
