package ch.ethz.inf.dbproject.sqlRevisited;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.ParsedQuery;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.SyntaxTreeDynamicNode;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.SyntaxTreeSelectionOperatorNode;

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
		SQLType[] setTypes = this.getTypesSet((SyntaxTreeDynamicNode)pq.getSyntaxTreeDynamicNode().dynamicChildren.get(1));
		int numbId = this.getNumberArguments((SyntaxTreeSelectionOperatorNode)pq.getSyntaxTreeDynamicNode().dynamicChildren.get(2));
		args = new Object[setTypes.length+numbId];
		typeArgs = new SQLType[]{setTypes[0], tc.getTableSchema().getKeys()[0]}; 
	}

	@Override
	public boolean execute() throws SQLException {
		boolean ret = false;
		lock.lock();
		try{
			ByteBuffer objToUpdate = ByteBuffer.allocate(tc.getTableSchema().getSizeOfEntry());
			if (tc.get((ByteBuffer)ByteBuffer.allocate(4).putInt((int)args[1]).rewind(), objToUpdate)){
				modifyObject((ByteBuffer)objToUpdate.rewind(), args[0], 5);
				objToUpdate.rewind();
				tc.delete((ByteBuffer)ByteBuffer.allocate(4).putInt((int)args[1]).rewind());
				objToUpdate.rewind();
				ret = tc.insert(objToUpdate);
				if (ret)
					countChanged = 1;
				else
					countChanged = 0;
			}
		} catch (Exception ex){
			ex.printStackTrace();
			countChanged = 0;
			throw new SQLException();
		} finally {
			lock.unlock();
		}
		return ret;
	}
	
	////
	//Private
	////
	
	private ByteBuffer modifyObject(ByteBuffer obj, Object src, int index){
		ByteBuffer buf = (ByteBuffer)Serializer.serializerObject(src, new SQLType(SQLType.BaseType.Boolean)).rewind();
		int positionToUpdate = 0;
		for (int i = 0; i < index; i++){
			positionToUpdate += tc.getTableSchema().getAttributesTypes()[i].byteSizeOfType();
		}
		int prePos = obj.position();
		obj.position(positionToUpdate);
		obj.put(buf);
		obj.position(prePos);
		return obj;
	}
	
	protected SQLType[] getTypesSet(SyntaxTreeDynamicNode dn){
		return new SQLType[]{new SQLType(SQLType.BaseType.Boolean)};
	}
	
	protected int getNumberArguments(SyntaxTreeSelectionOperatorNode stdn){
		if (stdn.getChild() != null){
			return this.getNumberArguments((SyntaxTreeSelectionOperatorNode)stdn.getChild())+1;
		} else {
			return 1;
		}
	}
}
