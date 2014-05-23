package ch.ethz.inf.dbproject.sqlRevisited;

import java.sql.Date;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.concurrent.locks.Lock;

public abstract class AbstractPreparedStatement implements PreparedStatement {

	protected Object[] args; 
	protected SQLType[] typeArgs;
	protected Lock lock;

	@Override
	public ResultSet getResultSet() throws SQLException {
		throw new SQLException();
	}

	@Override
	public void setInt(int index, int value) throws SQLException {
		if (typeArgs[index].type == SQLType.BaseType.Integer)
			args[index] = value;
		else
			throw new SQLTypeCheckException(typeArgs[index], new SQLType(SQLType.BaseType.Integer));
	}

	@Override
	public void setString(int index, String value) throws SQLException {
		if (typeArgs[index].type == SQLType.BaseType.Varchar)
			args[index] = value;
		else
			throw new SQLTypeCheckException(typeArgs[index], new SQLType(SQLType.BaseType.Varchar));
	}

	@Override
	public void setDate(int index, Date value) throws SQLException {
		if (typeArgs[index].type == SQLType.BaseType.Date)
			args[index] = value;
		else
			throw new SQLTypeCheckException(typeArgs[index], new SQLType(SQLType.BaseType.Date));	
	}

	@Override
	public void setTimeStamp(int index, Timestamp value) throws SQLException {
		if (typeArgs[index].type == SQLType.BaseType.Date || typeArgs[index].type == SQLType.BaseType.Datetime)
			args[index] = value;
		else
			throw new SQLTypeCheckException(typeArgs[index], new SQLType(SQLType.BaseType.Date));	
	}

	@Override
	public void setNull(int index, Types value) throws SQLException {
		args[index] = null;
		
	}

	@Override
	public void setObject(int index, Object value) throws SQLException {
		args[index] = value;
		
	}

	@Override
	public void setBoolean(int index, boolean value) throws SQLException {
		if (typeArgs[index].type == SQLType.BaseType.Boolean)
			args[index] = value;
		else
			throw new SQLTypeCheckException(typeArgs[index], new SQLType(SQLType.BaseType.Boolean));
	}
}
