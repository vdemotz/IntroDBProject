package ch.ethz.inf.dbproject.sqlRevisited.test;

import java.nio.ByteBuffer;
import java.util.Arrays;

import ch.ethz.inf.dbproject.sqlRevisited.PhysicalTableInterface;
import ch.ethz.inf.dbproject.sqlRevisited.SQLPhysicalException;
import ch.ethz.inf.dbproject.sqlRevisited.TableIterator;
import ch.ethz.inf.dbproject.sqlRevisited.TableSchema;

public class StaticPhysicalTable implements PhysicalTableInterface {

	/*
	 * The imposed order is the order given at creation time
	 */
	
	private final byte[] data;
	private final TableSchema schema;
	private final int tupleWidth;
	private final int numberOfTuples;
	
	StaticPhysicalTable(TableSchema schema, byte[] data) {
		this.data = data;
		this.schema = schema;
		this.tupleWidth = schema.getSizeOfEntry();
		this.numberOfTuples = data.length / tupleWidth;
		assert ((this.data.length % tupleWidth) == 0);
	}
	
	@Override
	public TableSchema getTableSchema() {
		return schema;
	}
	
	@Override
	public boolean delete(ByteBuffer key) throws SQLPhysicalException {
		throw new SQLPhysicalException();
	}

	@Override
	public boolean insert(ByteBuffer value) throws SQLPhysicalException {
		throw new SQLPhysicalException();
	}

	@Override
	public TableIterator getIterator(ByteBuffer key) throws SQLPhysicalException {
		throw new SQLPhysicalException();
	}

	@Override
	public TableIterator getIteratorFirst() throws SQLPhysicalException {
		return new StaticPhysicalTableIterator();
	}
	
	class StaticPhysicalTableIterator implements TableIterator {
		
		private int currentTuple = 0;
		
		StaticPhysicalTableIterator()
		{
			currentTuple = 0;
		}
		
		@Override
		public boolean next(ByteBuffer destination) throws SQLPhysicalException {
			if (currentTuple < numberOfTuples) {
				destination.put(data, currentTuple*tupleWidth, tupleWidth);
				currentTuple++;
				return true;
			} else {
				return false;
			}
		}
		
	}

}
