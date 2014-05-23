package ch.ethz.inf.dbproject.sqlRevisited;

import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import ch.ethz.inf.dbproject.sqlRevisited.Parser.ParsedQuery;

public class InsertPreparedStatement extends AbstractWritePreparedStatement {
	
	InsertPreparedStatement(ParsedQuery pq, WriteLock l, List<PhysicalTableInterface> lTables) throws SQLPhysicalException{
		super(pq, l, lTables);
		typeArgs = tc.getTableSchema().getAttributesTypes();
	}
	
	////
	//Public
	////

	@Override
	public boolean execute() throws SQLException {
		boolean ret = false;
		while(lock.tryLock()){
			;
		}
		try{
			ret = tc.insert(Serializer.serializerTuple(tc.getTableSchema(), args));
			if (ret)
				countChanged = 1;
			else
				countChanged = 0;
		} catch (Exception ex){
			lock.unlock();
			countChanged = 0;
			ex.printStackTrace();
			throw new SQLException();
		}
		lock.unlock();
		return ret;
	}
}
