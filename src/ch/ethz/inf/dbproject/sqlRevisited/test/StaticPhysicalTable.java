package ch.ethz.inf.dbproject.sqlRevisited.test;

import java.nio.ByteBuffer;
import java.util.Arrays;

import ch.ethz.inf.dbproject.sqlRevisited.PhysicalTableInterface;
import ch.ethz.inf.dbproject.sqlRevisited.SQLPhysicalException;
import ch.ethz.inf.dbproject.sqlRevisited.TableSchema;

public class StaticPhysicalTable implements PhysicalTableInterface {

	/*
	 * The imposed order is the order given at creation time
	 */
	
	private final byte[] data;
	private final TableSchema schema;
	private final int tupleWidth;
	private final int tupleKeyWidth;
	private final int numberOfTuples;
	private final byte[] temporaryKey;
	
	private int iterator = 1;
	
	StaticPhysicalTable(TableSchema schema, byte[] data) {
		this.data = data;
		this.schema = schema;
		this.tupleWidth = schema.getSizeOfEntry();
		this.numberOfTuples = data.length / tupleWidth;
		this.tupleKeyWidth = schema.getSizeOfKeys();
		this.temporaryKey = new byte[tupleKeyWidth];
		assert ((this.data.length % tupleWidth) == 0);
	}
	
	@Override
	public TableSchema getTableSchema() {
		return schema;
	}

	@Override
	public boolean get(ByteBuffer key, ByteBuffer destination) throws SQLPhysicalException {
		throw new SQLPhysicalException();
		/*key.get(temporaryKey, 0, tupleKeyWidth);
		int index = indexOf(temporaryKey);
		if (index >= 0) {
			destination.put(data, index*tupleWidth, tupleWidth);
			return true;
		} else {
			return false;
		}*/
	}

	@Override
	public boolean succ(ByteBuffer key, ByteBuffer destination) throws SQLPhysicalException {
		
		if (iterator < numberOfTuples) {
			destination.put(data, iterator*tupleWidth, tupleWidth);
			iterator++;
			return true;
		} else {
			return false;
		}
		
		/*key.get(temporaryKey, 0, tupleKeyWidth);
		int index = indexOf(temporaryKey);
		System.out.println(index);
		if (index >= 0 && index+1<numberOfTuples) {
			destination.put(data, (index+1)*tupleWidth, tupleWidth);
			return true;
		} else {
			return false;
		}*/
	}
	
	private int indexOf(byte[] key) {
		int index = 0;
		while (index < numberOfTuples) {
			byte[] currentTuple = Arrays.copyOfRange(data, index*tupleKeyWidth, (index+1)*tupleKeyWidth);
			if (Arrays.equals(currentTuple, key)) {
				return index;
			}
			index++;
		}
		return -1;
	}

	@Override
	public boolean min(ByteBuffer destination) throws SQLPhysicalException {
		if (data.length > 0) {
			destination.put(data, 0, tupleWidth);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean delete(ByteBuffer key) throws SQLPhysicalException {
		throw new SQLPhysicalException();
	}

	@Override
	public boolean insert(ByteBuffer value) throws SQLPhysicalException {
		throw new SQLPhysicalException();
	}

	
	
}
