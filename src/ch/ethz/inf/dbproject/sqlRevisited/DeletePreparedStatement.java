package ch.ethz.inf.dbproject.sqlRevisited;

import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.ParsedQuery;

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
		typeArgs = tc.getTableSchema().getKeys();
	}

	@Override
	public boolean execute() throws SQLException {
		boolean ret = false;
		while(lock.tryLock()){
			;
		}
		try{
			ret = tc.delete(Serializer.serializerTuple(tc.getTableSchema(), args));
		} catch (Exception ex){
			lock.unlock();
			ex.printStackTrace();
			throw new SQLException();
		}
		lock.unlock();
		return ret;
	}

	@Override
	public int getUpdateCount() {
		// TODO Auto-generated method stub
		return 0;
	}
}