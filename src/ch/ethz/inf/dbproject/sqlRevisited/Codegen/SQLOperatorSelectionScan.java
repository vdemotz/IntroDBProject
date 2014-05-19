package ch.ethz.inf.dbproject.sqlRevisited.Codegen;

import java.nio.ByteBuffer;
import java.util.Comparator;

import ch.ethz.inf.dbproject.sqlRevisited.SQLPhysicalException;
import ch.ethz.inf.dbproject.sqlRevisited.TableSchema;

public class SQLOperatorSelectionScan extends SQLOperatorUnary {

	public final Predicate<byte[]> predicate;
	public final ByteBuffer nextResult;
	boolean hasNext;
	
	public SQLOperatorSelectionScan(TableSchema schema, SQLOperator child, Predicate<byte[]> predicate) {
		super(schema, child);
		this.predicate = predicate;
		this.nextResult = ByteBuffer.wrap(new byte[schema.getSizeOfEntry()]);
	}
	
	@Override
	public void open() throws SQLPhysicalException {
		findNext();
	}

	@Override
	public boolean hasNext() {
		return hasNext;
	}

	@Override
	public void getNext(ByteBuffer resultBuffer) {
		assert(hasNext);
		nextResult.rewind();
		resultBuffer.put(nextResult);
	}
	
	@Override
	protected void internalRewind() throws SQLPhysicalException {
		findNext();
	}

	private void findNext() throws SQLPhysicalException {
		hasNext = false;
		while (getChild().hasNext()) {
			nextResult.rewind();
			getChild().getNext(nextResult);
			if (predicate.has(nextResult.array())) {
				hasNext = true;
				break;
			}
		}
	}
}
	

