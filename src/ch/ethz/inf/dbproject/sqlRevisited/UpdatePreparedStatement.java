package ch.ethz.inf.dbproject.sqlRevisited;

import java.util.concurrent.locks.Lock;

import ch.ethz.inf.dbproject.sqlRevisited.Parser.ParsedQuery;

public class UpdatePreparedStatement extends AbstractPreparedStatement {

	/**
	 * Create a new PrepareStatement update
	 * @param pq ParsedQuery of type UPDATE
	 * @param l a Write Lock
	 * @param db Database to acquire connection to tables
	 */
	UpdatePreparedStatement(ParsedQuery pq, Lock l, Database db){
		
	}

	@Override
	public ResultSet executeQuery() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean execute() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getUpdateCount() {
		// TODO Auto-generated method stub
		return 0;
	}
}
