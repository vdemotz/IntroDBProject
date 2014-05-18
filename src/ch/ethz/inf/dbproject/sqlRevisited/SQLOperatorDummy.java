package ch.ethz.inf.dbproject.sqlRevisited;

import java.nio.ByteBuffer;

public class SQLOperatorDummy extends SQLOperator {
	/**
	 * Represents a dummy physical table, supporting a linear scan.
	 */
	
	private final int toupleWidth;
	private int cur = 0;
	private final byte[] content;
	
	public SQLOperatorDummy(TableSchema schema, byte[] source) {
		super(schema);
		content = source;
		toupleWidth = schema.getSizeOfEntry();
		assert content.length % toupleWidth == 0;
	}

	@Override
	boolean hasNext() {
		return cur < content.length;
	}

	@Override
	void getNext(ByteBuffer resultBuffer) {
		assert (hasNext());
		resultBuffer.put(content, cur*toupleWidth, toupleWidth);
	}
	
	@Override
	protected void internalRewind() {
		cur = 0;
	}
	
}
