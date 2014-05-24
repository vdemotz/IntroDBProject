package ch.ethz.inf.dbproject.sqlRevisited;

import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import ch.ethz.inf.dbproject.sqlRevisited.Parser.ParsedQuery;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.SyntaxTreeDynamicNode;

public class InsertPreparedStatement extends AbstractWritePreparedStatement {
	
	InsertPreparedStatement(ParsedQuery pq, WriteLock l, List<PhysicalTableInterface> lTables) throws SQLPhysicalException{
		super(pq, l, lTables);
		int numbArgs = this.getNumberArguments((SyntaxTreeDynamicNode) pq.getSyntaxTreeDynamicNode().dynamicChildren.get(1));
		args = new Object[numbArgs];
		typeArgs = tc.getTableSchema().getAttributesTypes();
	}
	
	////
	//Public
	////

	@Override
	public boolean execute() throws SQLException {
		boolean ret = false;
		lock.lock();
		try{
			ret = tc.insert(Serializer.serializerTuple(tc.getTableSchema(), args));
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
}
