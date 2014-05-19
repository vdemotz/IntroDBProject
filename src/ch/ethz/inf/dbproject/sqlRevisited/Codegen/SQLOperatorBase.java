package ch.ethz.inf.dbproject.sqlRevisited.Codegen;

import java.nio.ByteBuffer;

import ch.ethz.inf.dbproject.sqlRevisited.SQLPhysicalException;
import ch.ethz.inf.dbproject.sqlRevisited.TableConnection;
import ch.ethz.inf.dbproject.sqlRevisited.TableSchema;

public class SQLOperatorBase extends SQLOperator {

	private final TableConnection physicalTable;
	private ByteBuffer currentResult;
	private ByteBuffer nextResult;
	private boolean hasNext;
	
	public SQLOperatorBase(TableSchema schema, TableConnection physicalTable) {
		super(schema);
		this.physicalTable = physicalTable;
		this.nextResult = ByteBuffer.wrap(new byte[schema.getSizeOfEntry()]);
		this.currentResult = ByteBuffer.wrap(new byte[schema.getSizeOfEntry()]);
	}
	
	void open() throws SQLPhysicalException {
		//TODO change exception type in PhysicalTable
		try {
			physicalTable.min(currentResult);
		} catch (Exception e) {
			throw new SQLPhysicalException();
		} 
	}

	@Override
	boolean hasNext() {
		return hasNext;
	}

	@Override
	void getNext(ByteBuffer resultBuffer) {
		resultBuffer.put(currentResult.array());
		findNext();
	}
	
	private void findNext() {
		currentResult.rewind();
		nextResult.rewind();
		//try to get the next tuple
		//TODO this is only correct if primary keys are stored at the beginning of the tuple by convention
		try{
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
