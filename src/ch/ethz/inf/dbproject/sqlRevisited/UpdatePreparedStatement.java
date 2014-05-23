package ch.ethz.inf.dbproject.sqlRevisited;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.ParsedQuery;

public class UpdatePreparedStatement extends AbstractWritePreparedStatement {

	/**
	 * Create a new PrepareStatement update
	 * @param pq ParsedQuery of type UPDATE
	 * @param l a Write Lock
	 * @param db Database to acquire connection to tables
	 * @throws SQLPhysicalException 
	 */
	UpdatePreparedStatement(ParsedQuery pq, WriteLock l, List<PhysicalTableInterface> lTables) throws SQLPhysicalException{
		super(pq, l,lTables);
		typeArgs = tc.getTableSchema().getKeys();
	}

	@Override
	public boolean execute() throws SQLException {
		boolean ret = false;
		while(lock.tryLock()){
			;
		}
		try{
			ByteBuffer objToUpdate = ByteBuffer.allocate(tc.getTableSchema().getSizeOfEntry());
			if (tc.get(Serializer.serializerTuple(tc.getTableSchema(), args), objToUpdate)){
				modifyObject((ByteBuffer)objToUpdate.rewind(), args[0], -1);
				objToUpdate.rewind();
				ret = tc.update(objToUpdate);
				if (ret)
					countChanged = 1;
				else
					countChanged = 0;
			}
		} catch (Exception ex){
			lock.unlock();
			ex.printStackTrace();
			countChanged = 0;
			throw new SQLException();
		}
		lock.unlock();
		return ret;
	}
	
	////
	//Private
	////
	
	private ByteBuffer modifyObject(ByteBuffer obj, Object src, int index){
		return null;
	}
}
