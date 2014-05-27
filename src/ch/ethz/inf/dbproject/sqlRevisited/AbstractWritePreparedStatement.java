package ch.ethz.inf.dbproject.sqlRevisited;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.ParsedQuery;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.SyntaxTreeDynamicNode;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.SyntaxTreeIdentifierNode;

abstract class AbstractWritePreparedStatement extends AbstractPreparedStatement {

	protected TableConnection tc;
	protected int countChanged;
	
	AbstractWritePreparedStatement(ParsedQuery pq, Lock l, List<PhysicalTableInterface> listTablesConnections) throws SQLPhysicalException{
		super(l);
		String tableName = ((SyntaxTreeIdentifierNode)pq.getSyntaxTreeDynamicNode().dynamicChildren.get(0)).generatingToken.content;
		
		try {
			for (int i = 0; i < listTablesConnections.size(); i++){
				if(((TableConnection)listTablesConnections.get(i)).getTableSchema().getTableName().equals(tableName)){
					tc = ((TableConnection)listTablesConnections.get(i));
				}
				
			}
		} catch (Exception e) {
			System.err.println("Unable to open table "+tableName);
			throw new SQLPhysicalException();
		}
		countChanged = 0;
	}
	
	@Override
	public ResultSet executeQuery() throws SQLException {
		throw new SQLException();
	}
	
	@Override
	public int getUpdateCount(){
		return countChanged;
	}
	
	////
	//Private
	////
	
	protected int getNumberArguments(SyntaxTreeDynamicNode stdn){
		try{
			SyntaxTreeDynamicNode child = (SyntaxTreeDynamicNode)stdn.dynamicChildren.get(0);
			return (this.getNumberArguments(child) + 1);
		} catch (Exception ex){
			return 1;
		}
	}
}
