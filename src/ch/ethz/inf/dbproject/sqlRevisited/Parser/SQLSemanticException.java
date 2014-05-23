package ch.ethz.inf.dbproject.sqlRevisited.Parser;

import ch.ethz.inf.dbproject.sqlRevisited.SQLException;

public class SQLSemanticException extends SQLException {

	public enum Type
	{
		NoSuchTableException("No table with name : "),
		NoSuchAttributeException("No attribute with name : "),
		InternalError("An internal error occurred : "),
		NotApplicableToTokenWithClass("Not applicable to token "),
		NotEnoughArgumentsProvided("Not enough arguments provided. "),
		TypeError("Type mismatch ");
		
		public final String message;
		Type(String message) {
			this.message = message;
		}
	}
	
	public final Type type;
	public final String errorAttribute;
	
	public SQLSemanticException(Type type) {
		errorAttribute = "";
		this.type = type;
	}
	
	public SQLSemanticException(Type type, String errorAttribute) {
		this.errorAttribute = errorAttribute;
		this.type = type;
	}
	
	public String toString()
	{
		return "SQL Semantic Error occured: " + type.message + errorAttribute;
	}
}
