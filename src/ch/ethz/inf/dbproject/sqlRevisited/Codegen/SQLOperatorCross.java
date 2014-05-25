package ch.ethz.inf.dbproject.sqlRevisited.Codegen;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import java.util.Arrays;

import ch.ethz.inf.dbproject.sqlRevisited.SQLPhysicalException;
import ch.ethz.inf.dbproject.sqlRevisited.TableSchema;

class SQLOperatorCross  extends SQLOperatorBinary {

	private final ByteBuffer currentLefthandTuple;
	private final ByteBuffer currentRighthandTuple;
	private boolean leftHas;
	
	public SQLOperatorCross(TableSchema schema, SQLOperator left, SQLOperator right) {
		super(schema, left, right);
		currentLefthandTuple = ByteBuffer.wrap(new byte[left.schema.getSizeOfEntry()]);
		currentRighthandTuple = ByteBuffer.wrap(new byte[right.schema.getSizeOfEntry()]);
	}
	
	////
	//SQLOperator Interface
	////
	
	@Override
	public boolean next(ByteBuffer resultBuffer) throws SQLPhysicalException {
		
		//try to get the next right result
		currentRighthandTuple.rewind();
		boolean rightHas = getRightChild().next(currentRighthandTuple);
		//if there is none left
		if (!rightHas) {
			//rewind and retry
			getRightChild().rewind();
			currentRighthandTuple.rewind();
			rightHas = getRightChild().next(currentRighthandTuple);
			//and get the next left result
			currentLefthandTuple.rewind();
			leftHas = getLeftChild().next(currentLefthandTuple);
		}
		
		if (leftHas && rightHas) {
			//if there are results on both sides, return their concatenation
			resultBuffer.put(currentLefthandTuple.array());
			resultBuffer.put(currentRighthandTuple.array());
			return true;
		} else {
			//otherwise the results are done
			return false;
		}
	}
	
	////
	//INTERNAL
	////
	
	@Override
	protected void internalRewind() throws SQLPhysicalException {
		internalOpen();
	}
	
	@Override
	protected void internalOpen()  throws SQLPhysicalException {
		currentLefthandTuple.rewind();
		leftHas = getLeftChild().next(currentLefthandTuple);
	}
}
