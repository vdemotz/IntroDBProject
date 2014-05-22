package ch.ethz.inf.dbproject.sqlRevisited.Codegen;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Comparator;

import ch.ethz.inf.dbproject.sqlRevisited.SQLPhysicalException;
import ch.ethz.inf.dbproject.sqlRevisited.SQLType;
import ch.ethz.inf.dbproject.sqlRevisited.Serializer;
import ch.ethz.inf.dbproject.sqlRevisited.TableSchema;

public class SQLOperatorAggregateMax extends SQLOperatorAggregate implements SQLOperatorGrouping {

	private final int startByteOffset;
	private final int endByteOffset;
	private final ByteBuffer currentMax;
	private final ByteBuffer intermediate;
	private boolean fresh = true;
	private final Comparator<byte[]> comparator;
	
	public SQLOperatorAggregateMax(TableSchema schema, SQLOperator child, Comparator<byte[]> comparator, int attributeStartByteOffset, int attributeEndOffset) {
		super(schema, child);
		
		this.comparator = comparator;
		this.currentMax = ByteBuffer.wrap(new byte[child.schema.getSizeOfEntry()]);
		this.intermediate = ByteBuffer.wrap(new byte[child.schema.getSizeOfEntry()]);
		this.startByteOffset = attributeStartByteOffset;
		this.endByteOffset = attributeEndOffset;
	}

	@Override
	public boolean next(ByteBuffer resultBuffer) throws SQLPhysicalException {
		
		int lastGroup = getChild().getGroup();
		
		intermediate.rewind();
		boolean hasNext = getChild().next(intermediate);
		if (getChild().getGroup() != lastGroup) {//if the returned group has just changed group, reset the counter
			fresh = true;
		}
		if (hasNext) {
			if (fresh || comparator.compare(intermediate.array(), currentMax.array()) > 0) {//set new max
				currentMax.rewind();
				currentMax.put(intermediate.array());
				fresh = false;
			}
			resultBuffer.put(intermediate.array());
			resultBuffer.put(Arrays.copyOfRange(currentMax.array(), startByteOffset, endByteOffset));
		}
		return hasNext;
	}

}
