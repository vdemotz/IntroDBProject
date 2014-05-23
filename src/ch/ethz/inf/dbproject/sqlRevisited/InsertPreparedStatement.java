package ch.ethz.inf.dbproject.sqlRevisited;

import ch.ethz.inf.dbproject.sqlRevisited.Parser.ParsedQuery;

public class InsertPreparedStatement  extends AbstractPreparedStatement {

	private TableConnection tc;
	
	/**
	 * Create a new PrepareStatement insert
	 * @param pq ParsedQuery of type INSERT
	 */
	InsertPreparedStatement(ParsedQuery pq){
		System.out.println(pq.getSyntaxTreeDynamicNode().dynamicChildren.get(0).toString());
		System.out.println(pq.getSyntaxTreeDynamicNode().dynamicChildren.get(1).toString());
		//System.out.println(pq.getSyntaxTreeDynamicNode().dynamicChildren.get(0).toString());
		
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
