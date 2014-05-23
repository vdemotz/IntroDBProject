package ch.ethz.inf.dbproject.sqlRevisited;

import java.util.List;
import java.util.concurrent.locks.Lock;

import ch.ethz.inf.dbproject.sqlRevisited.Parser.ParsedQuery;

public class SelectPreparedStatement  extends AbstractPreparedStatement {

	/**
	 * Create a new PreparedStatement query
	 * @param pq ParsedQuery of type SELECT
	 * @param l a Read Lock
	 * @param db Database to acquire connection to tables
	 */
	public SelectPreparedStatement(ParsedQuery pq, Lock l, List<PhysicalTableInterface> lTables){
		
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
