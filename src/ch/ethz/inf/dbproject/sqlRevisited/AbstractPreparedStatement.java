package ch.ethz.inf.dbproject.sqlRevisited;

import java.util.Date;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.locks.Lock;

public abstract class AbstractPreparedStatement implements PreparedStatement {
	
	protected Object[] args; 
	protected SQLType[] typeArgs;
	protected final Lock lock;
	protected final DateFormat dateFormatter = new SimpleDateFormat(SQLType.DATE_FORMAT_STRING);
	protected final DateFormat datetimeFormatter = new SimpleDateFormat(SQLType.DATETIME_FORMAT_STRING);
	
	AbstractPreparedStatement(Lock lock) {
		this.lock = lock;
	}
	
	@Override
	public ResultSet getResultSet() throws SQLException {
		return null;
	}

	@Override
	public void setInt(int index, int value) throws SQLException {
		index--;
		if (typeArgs == null || typeArgs[index].type == SQLType.BaseType.Integer)
			args[index] = value;
		else
			throw new SQLTypeCheckException(typeArgs[index], new SQLType(SQLType.BaseType.Integer));
	}

	@Override
	public void setString(int index, String value) throws SQLException {
		index--;
		if (typeArgs == null || typeArgs[index].type == SQLType.BaseType.Varchar)
			args[index] = value;
		else
			throw new SQLTypeCheckException(typeArgs[index], new SQLType(SQLType.BaseType.Varchar));
	}

	@Override
	public void setDate(int index, Date value) throws SQLException {
		index--;
		if (typeArgs == null || typeArgs[index].type == SQLType.BaseType.Date)
			args[index] = dateFormatter.format(value);
		else
			throw new SQLTypeCheckException(typeArgs[index], new SQLType(SQLType.BaseType.Date));	
	}

	@Override
	public void setTimestamp(int index, Date value) throws SQLException {
		index--;
		if (typeArgs == null || typeArgs[index].type == SQLType.BaseType.Date || typeArgs[index].type == SQLType.BaseType.Datetime)
			args[index] = datetimeFormatter.format(value);
		else
			throw new SQLTypeCheckException(typeArgs[index], new SQLType(SQLType.BaseType.Date));	
	}

	@Override
	public void setNull(int index, Types value) throws SQLException {
		index--;
		args[index] = null;
		
	}

	@Override
	public void setObject(int index, Object value) throws SQLException {
		index--;
		args[index] = value;
		
	}

	@Override
	public void setBoolean(int index, boolean value) throws SQLException {
		index--;
		if (typeArgs == null || typeArgs[index].type == SQLType.BaseType.Boolean)
			args[index] = value;
		else
			throw new SQLTypeCheckException(typeArgs[index], new SQLType(SQLType.BaseType.Boolean));
	}
}
