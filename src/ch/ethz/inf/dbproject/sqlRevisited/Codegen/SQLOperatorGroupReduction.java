package ch.ethz.inf.dbproject.sqlRevisited.Codegen;

import java.nio.ByteBuffer;

import ch.ethz.inf.dbproject.sqlRevisited.SQLPhysicalException;
import ch.ethz.inf.dbproject.sqlRevisited.TableSchema;

class SQLOperatorGroupReduction extends SQLOperatorUnary {

	private ByteBuffer lastResult;
	private ByteBuffer nextResult;
	private boolean childHasNext = false;
	
	public SQLOperatorGroupReduction(TableSchema schema, SQLOperator child) {
		super(schema, child);
		nextResult = ByteBuffer.wrap(new byte[schema.getSizeOfEntry()]);
		lastResult = ByteBuffer.wrap(new byte[schema.getSizeOfEntry()]);
	}
	
	@Override
	protected void internalOpen() throws SQLPhysicalException {
		internalRewind();
	}
	
	@Override
	protected void internalRewind() throws SQLPhysicalException {
		childHasNext = getChild().next(lastResult);
	}

	@Override
	public boolean next(ByteBuffer resultBuffer) throws SQLPhysicalException {
		nextResult.rewind();
		boolean hasNext = childHasNext;//if the child has at least one result, we have a result
		int currentGroup = getChild().getGroup();
		childHasNext = getChild().next(nextResult);
		while (childHasNext && getChild().getGroup()==currentGroup) {//choose the last element of the group
			swapBuffers();
			nextResult.rewind();
			childHasNext = getChild().next(nextResult);
		}
		if (hasNext) {
			resultBuffer.put(lastResult.array());
		}
		swapBuffers();
		return hasNext;
	}
	
	private void swapBuffers() {
		ByteBuffer temp = lastResult;
		lastResult = nextResult;
		nextResult = temp;
	}
	
}
