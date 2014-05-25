package ch.ethz.inf.dbproject.sqlRevisited;

import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.ParsedQuery;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.SyntaxTreeSelectionOperatorNode;

public class DeletePreparedStatement  extends AbstractWritePreparedStatement {

	/**
	 * Create a new PrepareStatement update
	 * @param pq ParsedQuery of type DELETE
	 * @param l a Write Lock
	 * @param db Database to acquire connection to tables
	 * @throws SQLPhysicalException 
	 */
	public DeletePreparedStatement(ParsedQuery pq, WriteLock l, List<PhysicalTableInterface> lTables) throws SQLPhysicalException{
		super(pq, l, lTables);
		int numbArgs = this.getNumberArguments((SyntaxTreeSelectionOperatorNode)pq.getSyntaxTreeDynamicNode().dynamicChildren.get(1));
		args = new Object[numbArgs];
		typeArgs = tc.getTableSchema().getKeys();
	}

	@Override
	public boolean execute() throws SQLException {
		boolean ret = false;
		lock.lock();
		try{
			ret = tc.delete(Serializer.serializerTuple(tc.getTableSchema(), args));
			if (ret)
				countChanged = 1;
			else
				countChanged = 0;
		} catch (Exception ex){
			countChanged = 0;
			ex.printStackTrace();
			throw new SQLException();
		} finally {
			lock.unlock();
		}
		return ret;
	}
	
	
	protected int getNumberArguments(SyntaxTreeSelectionOperatorNode stdn){
		if (stdn.getChild() != null){
			return this.getNumberArguments((SyntaxTreeSelectionOperatorNode)stdn.getChild())+1;
		} else {
			return 1;
		}
	}
}
