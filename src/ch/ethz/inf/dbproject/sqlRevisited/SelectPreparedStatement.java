package ch.ethz.inf.dbproject.sqlRevisited;

import java.sql.Date;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;

import ch.ethz.inf.dbproject.sqlRevisited.Parser.SQLToken;

public class SelectPreparedStatement implements PreparedStatement{

	/**
	 * Create a new PreparedStatement query
	 * @param selectTokensStream
	 */
	public SelectPreparedStatement(ArrayList<SQLToken> selectTokensStream){
		
	}
	
	@Override
	public ResultSet executeQuery() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean execute() {
		// TODO Auto-generated method stub
		return true;
	}

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
