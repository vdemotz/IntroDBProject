package ch.ethz.inf.dbproject.sqlRevisited.Codegen;

import java.nio.ByteBuffer;

import ch.ethz.inf.dbproject.sqlRevisited.PhysicalTableInterface;
import ch.ethz.inf.dbproject.sqlRevisited.SQLPhysicalException;
import ch.ethz.inf.dbproject.sqlRevisited.TableConnection;
import ch.ethz.inf.dbproject.sqlRevisited.TableSchema;

public class SQLOperatorBase extends SQLOperator {

	private final PhysicalTableInterface physicalTable;
	private ByteBuffer currentResult;
	private ByteBuffer nextResult;
	private boolean hasNext;
	
	public SQLOperatorBase(TableSchema schema, PhysicalTableInterface physicalTable) {
		super(schema);
		this.physicalTable = physicalTable;
		this.nextResult = ByteBuffer.wrap(new byte[schema.getSizeOfEntry()]);
		this.currentResult = ByteBuffer.wrap(new byte[schema.getSizeOfEntry()]);
	}
	
<<<<<<< HEAD
	void open() throws SQLPhysicalException {
		//TODO change exception type in PhysicalTable
		try {
			physicalTable.min(currentResult);
		} catch (Exception e) {
			throw new SQLPhysicalException();
		} 
=======
	public void open() throws SQLPhysicalException {
		hasNext = physicalTable.min(currentResult);
>>>>>>> alternatePhysicalInterface
	}

	@Override
	public boolean hasNext() {
		return hasNext;
	}

	@Override
	public void getNext(ByteBuffer resultBuffer) throws SQLPhysicalException {
		assert hasNext;
		resultBuffer.put(currentResult.array());
		findNext();
	}
	
	private void findNext() throws SQLPhysicalException {
		currentResult.rewind();
		nextResult.rewind();
		//try to get the next tuple
<<<<<<< HEAD
		//TODO this is only correct if primary keys are stored at the beginning of the tuple by convention
		try{
=======
>>>>>>> alternatePhysicalInterface
		hasNext = physicalTable.succ(currentResult, nextResult);
		} catch (Exception ex){
			ex.printStackTrace();
		}
		//swap next with current
		ByteBuffer temp = currentResult;
		currentResult = nextResult;
		nextResult = temp;
	}
	
}
