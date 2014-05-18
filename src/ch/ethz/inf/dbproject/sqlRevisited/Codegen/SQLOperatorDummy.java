package ch.ethz.inf.dbproject.sqlRevisited.Codegen;

import java.nio.ByteBuffer;

import ch.ethz.inf.dbproject.sqlRevisited.SQLPhysicalException;
import ch.ethz.inf.dbproject.sqlRevisited.TableSchema;

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
	void getNext(ByteBuffer resultBuffer) throws SQLPhysicalException {
		assert (hasNext());
		resultBuffer.put(content, cur*toupleWidth, toupleWidth);
	}
	
	@Override
	protected void internalRewind() throws SQLPhysicalException {
		cur = 0;
	}

	@Override
	void open() throws SQLPhysicalException {
	}
	
}
