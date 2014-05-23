package ch.ethz.inf.dbproject.sqlRevisited;

import ch.ethz.inf.dbproject.sqlRevisited.Parser.ParsedQuery;

public class DeletePreparedStatement  extends AbstractPreparedStatement {

	/**
	 * Create a new PrepareStatement update
	 * @param pq ParsedQuery of type DELETE
	 */
	public DeletePreparedStatement(ParsedQuery pq){
		
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
