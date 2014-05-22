package ch.ethz.inf.dbproject.sqlRevisited.Codegen;

import java.nio.ByteBuffer;
import java.util.Comparator;

import ch.ethz.inf.dbproject.sqlRevisited.SQLPhysicalException;
import ch.ethz.inf.dbproject.sqlRevisited.TableSchema;

public class SQLOperatorSortGroup extends SQLOperatorMergeSort implements SQLOperatorGrouping {
	
	private int groupCount = 0;
	
	public SQLOperatorSortGroup(TableSchema schema, SQLOperator child, Comparator<byte[]> comparator) {
		super(schema, child, comparator);
	}
	
	@Override
	public boolean next(ByteBuffer resultBuffer) throws SQLPhysicalException {
		if (currentIndex < elementList.size()) {
			resultBuffer.put(elementList.get(currentIndex));
			//The group changed if the next element is different w.r.t. the comparator than the last value
			if (currentIndex > 0 && comparator.compare(elementList.get(currentIndex), elementList.get(currentIndex-1)) != 0) {
				groupCount++;
			}
			currentIndex++;
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	protected int getGroup(){
		return groupCount;
	}

	@Override
	protected void internalRewind()
	{
		currentIndex = 0;
		groupCount = 0;
	}
}
