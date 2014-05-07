package ch.ethz.inf.dbproject.sqlRevisited;

public class SQLParseException extends Exception{

	public final SQLToken.SQLTokenClass expectedToken;
	public final int atTokenPosition;
	
	SQLParseException(SQLToken.SQLTokenClass expectedToken, int atTokenPosition) {
		this.atTokenPosition = atTokenPosition;
		this.expectedToken = expectedToken;
	}
	
	SQLParseException(int atTokenPosition) {
		this.atTokenPosition = atTokenPosition;
		this.expectedToken = null;
	}
	
	@Override
	public String toString()
	{
		String result = this.getClass().getName() + " occurred at token position " + atTokenPosition;
		if (expectedToken != null) result += " expected token class: " + expectedToken;
		return result;
	}
}
