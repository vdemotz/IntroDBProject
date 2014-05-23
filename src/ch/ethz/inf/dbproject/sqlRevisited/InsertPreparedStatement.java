package ch.ethz.inf.dbproject.sqlRevisited;

import ch.ethz.inf.dbproject.sqlRevisited.Parser.ParsedQuery;

public class InsertPreparedStatement  extends AbstractPreparedStatement {

	/**
	 * Create a new PrepareStatement insert
	 * @param pq ParsedQuery of type INSERT
	 */
	InsertPreparedStatement(ParsedQuery pq){
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
