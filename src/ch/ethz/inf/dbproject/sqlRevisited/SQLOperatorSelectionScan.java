package ch.ethz.inf.dbproject.sqlRevisited;

import java.nio.ByteBuffer;
import java.util.Comparator;

public class SQLOperatorSelectionScan extends SQLOperatorUnary {

	public final Predicate<byte[]> predicate;
	public final ByteBuffer nextResult;
	boolean hasNext;
	
	public SQLOperatorSelectionScan(TableSchema schema, SQLOperator child, Predicate<byte[]> predicate) {
		super(schema, child);
		this.predicate = predicate;
		this.nextResult = ByteBuffer.wrap(new byte[schema.getSizeOfEntry()]);
		findNext();
	}

	@Override
	boolean hasNext() {
		return hasNext;
	}

	@Override
	void getNext(ByteBuffer resultBuffer) {
		assert(hasNext);
		nextResult.rewind();
		resultBuffer.put(nextResult);
	}
	
	private void findNext() {
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
	
	protected void internalRewind()
	{
		findNext();
	}
}
