package ch.ethz.inf.dbproject.sqlRevisited;

import java.util.concurrent.locks.Lock;

import ch.ethz.inf.dbproject.sqlRevisited.Parser.ParsedQuery;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.SyntaxTreeDynamicNode;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.SyntaxTreeIdentifierNode;

public class InsertPreparedStatement  extends AbstractPreparedStatement {

	private TableConnection tc;
	
	/**
	 * Create a new PrepareStatement insert
	 * @param pq ParsedQuery of type INSERT
	 * @param l a Write Lock
	 * @param db Database to acquire connection to tables
	 * @throws SQLPhysicalException 
	 */
	InsertPreparedStatement(ParsedQuery pq, Lock l, Database db) throws SQLPhysicalException{
		super(l);
		
		String tableName = ((SyntaxTreeIdentifierNode)pq.getSyntaxTreeDynamicNode().dynamicChildren.get(0)).generatingToken.content;
		try {
			db.getTableConnection(tableName);
		} catch (Exception e) {
			System.err.println("Unable to open table "+tableName);
			throw new SQLPhysicalException();
		}
		
		System.out.println(tableName);
		System.out.println(pq.getSyntaxTreeDynamicNode().dynamicChildren.get(1).toString());
		System.out.println("Number arguments : "+(1+this.getNumberArguments((SyntaxTreeDynamicNode) pq.getSyntaxTreeDynamicNode().dynamicChildren.get(1))));
		//System.out.println(pq.getSyntaxTreeDynamicNode().dynamicChildren.get(0).toString());
		
	}
	
	////
	//Public
	////

	@Override
	public ResultSet executeQuery() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean execute() throws SQLException {
		throw new SQLException();
	}

	@Override
	public int getUpdateCount() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	////
	//Private
	////
	
	private int getNumberArguments(SyntaxTreeDynamicNode stdn){
		try{
			SyntaxTreeDynamicNode child = (SyntaxTreeDynamicNode)stdn.dynamicChildren.get(0);
			System.out.println(child.toString());
			return (this.getNumberArguments(child) + 1);
		} catch (Exception ex){
			return 0;
		}
	}
}
