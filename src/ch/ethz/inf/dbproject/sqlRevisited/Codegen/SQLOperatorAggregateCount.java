package ch.ethz.inf.dbproject.sqlRevisited.Codegen;

import java.nio.ByteBuffer;

import ch.ethz.inf.dbproject.sqlRevisited.SQLPhysicalException;
import ch.ethz.inf.dbproject.sqlRevisited.SQLType;
import ch.ethz.inf.dbproject.sqlRevisited.Serializer;
import ch.ethz.inf.dbproject.sqlRevisited.TableSchema;

public class SQLOperatorAggregateCount extends SQLOperatorAggregate {

	private int groupCount;
	
	public SQLOperatorAggregateCount(TableSchema schema, SQLOperator child) {
		super(schema, child);
	}

	@Override
	protected void internalOpen() {
		internalRewind();
	}
	
	@Override
	protected void internalRewind() {
		groupCount = 0;
	}
	
	@Override
	public boolean next(ByteBuffer resultBuffer) throws SQLPhysicalException {
		
		int lastGroup = getChild().getGroup();
		
		boolean hasNext = getChild().next(resultBuffer);
		if (getChild().getGroup() != lastGroup) {//if the returned group has just changed group, reset the counter
			groupCount = 0;
		}
		if (hasNext) {
			groupCount += 1;
			Serializer.putBytes(groupCount, SQLType.INTEGER, resultBuffer);
		}
		return hasNext;
	}

	@Override
	public SQLOperator copyWithSchema(TableSchema schema) {
		return new SQLOperatorAggregateCount(schema, getChild());
	}

}
