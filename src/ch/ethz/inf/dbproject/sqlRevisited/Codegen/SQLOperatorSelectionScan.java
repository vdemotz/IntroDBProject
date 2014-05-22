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
	public boolean next(ByteBuffer resultBuffer) throws SQLPhysicalException {
		boolean hasNext = false;
		nextResult.rewind();
		while (getChild().next(nextResult)) {
			nextResult.rewind();
			if (predicate.has(nextResult.array())) {
				hasNext = true;
				resultBuffer.put(nextResult.array());
				break;
			}
		}
		return hasNext;
	}
	
}
	

