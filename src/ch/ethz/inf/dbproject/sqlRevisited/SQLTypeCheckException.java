package ch.ethz.inf.dbproject.sqlRevisited;

public class SQLTypeCheckException extends SQLException {

	private static final long serialVersionUID = 1L;

	public SQLTypeCheckException(SQLType expected, SQLType current){
		System.err.println("Wrong sql type, expected : "+expected.toString() +" , but current : "+current.toString());
		this.printStackTrace();
	}

}
