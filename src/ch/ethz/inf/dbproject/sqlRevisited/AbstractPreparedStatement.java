package ch.ethz.inf.dbproject.sqlRevisited;

import java.sql.Date;
import java.sql.Timestamp;
import java.sql.Types;

public abstract class AbstractPreparedStatement implements PreparedStatement {

	private Object[] args; 

	@Override
	public ResultSet getResultSet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setInt(int index, int value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setString(int index, String value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDate(int index, Date value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTimeStamp(int index, Timestamp value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setNull(int index, Types value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setObject(int index, Object value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBoolean(int index, boolean value) {
		// TODO Auto-generated method stub
		
	}


}
