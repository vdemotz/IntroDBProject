package ch.ethz.inf.dbproject.sqlRevisited.Codegen;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import java.util.Arrays;

import ch.ethz.inf.dbproject.sqlRevisited.SQLPhysicalException;
import ch.ethz.inf.dbproject.sqlRevisited.TableSchema;

public class SQLOperatorCross  extends SQLOperatorBinary {

	private final ByteBuffer currentLefthandTuple;
	
	public SQLOperatorCross(TableSchema schema, SQLOperator left, SQLOperator right) {
		super(schema, left, right);
		
		if (left.hasNext()) {
			currentLefthandTuple = ByteBuffer.wrap(new byte[getLeftChild().schema.getSizeOfKeys()]);
		} else {
			currentLefthandTuple = null;//indicates the left relation is empty
		}
	}
	
	////
	//SQLOperator Interface
	////
	
	@Override
	public boolean hasNext() {
		//if the left relation is not exhausted, there are more results
		//if the left relation is exhausted, then there are results as long as the right relation is not exhausted and the left relation was not empty
		return getLeftChild().hasNext() || (currentLefthandTuple != null && getRightChild().hasNext());
	}

	@Override
	public void getNext(ByteBuffer resultBuffer) throws SQLPhysicalException {
		assert (hasNext());
		
		if (!getRightChild().hasNext()) {//if the right relation is exhausted, go to the next left tuple and rewind the right relation
			getLeftChild().getNext(currentLefthandTuple);
			getRightChild().rewind();
		}
		
		resultBuffer.put(currentLefthandTuple.array());
		getRightChild().getNext(resultBuffer);
	}
	
	////
	//INTERNAL
	////
	
	@Override
	protected void internalRewind() throws SQLPhysicalException
	{
		open();
	}
	
	public void open()  throws SQLPhysicalException {
		if (getLeftChild().hasNext()) {
			getLeftChild().getNext(currentLefthandTuple);
		}
	}
}
