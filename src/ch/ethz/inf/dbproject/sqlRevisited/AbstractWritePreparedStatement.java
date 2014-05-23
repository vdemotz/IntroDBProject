package ch.ethz.inf.dbproject.sqlRevisited;

import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.ParsedQuery;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.SyntaxTreeDynamicNode;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.SyntaxTreeIdentifierNode;

public abstract class AbstractWritePreparedStatement extends AbstractPreparedStatement {

	protected TableConnection tc;
	
	AbstractWritePreparedStatement(ParsedQuery pq, WriteLock l, List<PhysicalTableInterface> listTablesConnections) throws SQLPhysicalException{
		
		String tableName = ((SyntaxTreeIdentifierNode)pq.getSyntaxTreeDynamicNode().dynamicChildren.get(0)).generatingToken.content;
		
		try {
			//tc = db.getTableConnection(tableName);
		} catch (Exception e) {
			System.err.println("Unable to open table "+tableName);
			throw new SQLPhysicalException();
		}
		
		int numbArgs = this.getNumberArguments((SyntaxTreeDynamicNode) pq.getSyntaxTreeDynamicNode().dynamicChildren.get(1))+1;
		args = new Object[numbArgs];
		lock = l;

		//System.out.println(tableName);
		//System.out.println("Number arguments : "+(1+this.getNumberArguments((SyntaxTreeDynamicNode) pq.getSyntaxTreeDynamicNode().dynamicChildren.get(1))));
	}
	
	@Override
	public ResultSet executeQuery() throws SQLException {
		throw new SQLException();
	}
	
	////
	//Private
	////
	
	protected int getNumberArguments(SyntaxTreeDynamicNode stdn){
		try{
			SyntaxTreeDynamicNode child = (SyntaxTreeDynamicNode)stdn.dynamicChildren.get(0);
			return (this.getNumberArguments(child) + 1);
		} catch (Exception ex){
			return 0;
		}
	}
}
