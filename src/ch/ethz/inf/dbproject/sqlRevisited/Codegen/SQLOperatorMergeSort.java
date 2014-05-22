package ch.ethz.inf.dbproject.sqlRevisited.Codegen;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import ch.ethz.inf.dbproject.sqlRevisited.SQLPhysicalException;
import ch.ethz.inf.dbproject.sqlRevisited.TableSchema;

public class SQLOperatorMergeSort extends SQLOperatorUnary {

	protected final Comparator<byte[]> comparator;

	protected final ArrayList<byte[]> elementList = new ArrayList<byte[]>();
	protected int currentIndex = 0;
	
	public SQLOperatorMergeSort(TableSchema schema, SQLOperator child, Comparator<byte[]> comparator) {
		super(schema, child);
		this.comparator = comparator;
	}

	@Override
	protected void internalOpen() throws SQLPhysicalException {
		int size = schema.getSizeOfEntry();
		byte[] next = new byte[size];
		ByteBuffer nextBuffer = ByteBuffer.wrap(next);
		
		while (getChild().next(nextBuffer)) {
			elementList.add(next);
			next = new byte[size];
			nextBuffer = ByteBuffer.wrap(next);
		}
		Collections.sort(elementList, comparator);
	}
	
	@Override
	public boolean next(ByteBuffer resultBuffer) throws SQLPhysicalException {
		if (currentIndex < elementList.size()) {
			resultBuffer.put(elementList.get(currentIndex));
			currentIndex++;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public SQLOperator copyWithSchema(TableSchema schema) {
		return new SQLOperatorMergeSort(schema, getChild(), comparator);
	}
	
	@Override
	protected void internalRewind() {
		currentIndex = 0;
	}

}
