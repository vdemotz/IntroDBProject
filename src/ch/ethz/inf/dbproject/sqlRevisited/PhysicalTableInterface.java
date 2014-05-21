package ch.ethz.inf.dbproject.sqlRevisited;

import java.nio.ByteBuffer;
import java.util.List;

public interface PhysicalTableInterface {
	/*
	 * A Physical Table represents an ordered map or multimap of serialized objects.
	 * Each implementation defines an order over the possible values. This order may or may not have any meaning and may be disrupted by modifying operations.
	 * A table schema associated with the table defines the interpretation of the byte buffers and their sizes.
	 */
	
	/**
	 * Get the TableSchema for this table
	 * Note that by convention, keys are stored at the beginning of the tuples.
	 * @return the table schema associated with this table
	 */
	public TableSchema getTableSchema();
	
	/**
	 * Given a ByteBuffer with the position at the beginning of a tuple key, removes the tuple associated with this key from the table, if such a tuple exists.
	 * @param key a buffer with position at the beginning of a tuple key. The contents are to be interpreted by the table schema, that is its prefix containing the key.
	 * @return true, if a tuple with this key was deleted, false otherwise
	 * @throws SQLPhysicalException if an error occurred or the operation is not supported
	 */
	public boolean delete(ByteBuffer key) throws SQLPhysicalException;
	
	/**
	 * Given a ByteBuffer with the position at the beginning of a tuple, inserts the tuple into the table
	 * @param value a buffer with position at the beginning of a tuple. The contents are to be interpreted by the table schema.
	 * @return true if the value was inserted, false otherwise
	 * @throws SQLPhysicalException if an error occurred or the operation is not supported
	 */
	public boolean insert(ByteBuffer value) throws SQLPhysicalException;
	
	/**
	 * Create an iterator to have sequential access to a table.
	 * The sizes of the entries are determined by this tables TableSchema.
	 * @param key the key of the first object to search from
	 * @return a TableIterator if key valid, null otherwise
	 * @throws SQLPhysicalException if an error occurred or the operation is not supported
	 */
	public TableIterator getIterator(ByteBuffer key) throws SQLPhysicalException;
	
	/**
	 * Create an iterator which points to the first entry of the table
	 * The sizes of the entries are determined by this tables TableSchema.
	 * @return a TableIterator which points on first entry of the table
	 * @throws SQLPhysicalException if an error occurred
	 */
	public TableIterator getIteratorFirst() throws SQLPhysicalException;
	
	//public boolean update(ByteBuffer oldKey, ByteBuffer newValue);
}
