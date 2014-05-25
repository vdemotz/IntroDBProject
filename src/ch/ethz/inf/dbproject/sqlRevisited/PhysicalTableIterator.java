package ch.ethz.inf.dbproject.sqlRevisited;

import java.nio.ByteBuffer;

public class PhysicalTableIterator implements TableIterator{

	private final TableSchema tableSchema;
	private final int[] positionsEntries;
	private final TableConnection tableConnection;
	private int position;
	
	/**
	 * Create a new Iterator linked to a table
	 * @param keys of the object the iterator points to. (null if from first entry of table)
	 * @param tc a particular connection to a table;
	 * @param positionsEntries in order array of position of the entries in table connection
	 */
	PhysicalTableIterator(ByteBuffer keys, TableConnection tc){
		this.tableSchema = tc.getTableSchema();
		this.tableConnection = tc;
		this.positionsEntries = this.getPositionsEntries(tc.getStructureConnection());
		if (keys == null)
			this.position = 0;
		else
			this.position = this.getInitialPosition(tc.getStructureConnection(), keys);
	}
	
	public boolean next(ByteBuffer destination) throws SQLPhysicalException{
		boolean ret = false;
		if (positionsEntries == null || positionsEntries.length == 0)
			return ret;
		else if (position < positionsEntries.length)
			ret = tableConnection.readFromData(positionsEntries[position], tableSchema.getSizeOfEntry(), destination.array());
		else
			return ret;
		position++;
		return ret;
	}
	
	/**
	 * Rewind the iterator
	 * @return true
	 */
	public boolean rewind(){
		if (positionsEntries == null || positionsEntries.length == 0)
			return false;
		position = 0;
		return true;
	}
	
	private int getInitialPosition(StructureConnection sc, ByteBuffer keys){
		try {
			return sc.getPositionsForKeys(keys);
		} catch (Exception e) {
			return -1;
		}
	}
	
	private int[] getPositionsEntries(StructureConnection sc){
		return sc.getPositionsInOrder();
	}
}
