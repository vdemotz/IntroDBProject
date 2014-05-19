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
	 * Given a ByteBuffer with the position at the beginning of a tuple key, writes out the bytes corresponding to the full tuple to the specified destination if any tuple with such key exists.
	 * @param key a buffer with position at the beginning of a tuple key. The contents are to be interpreted by the table schema, that is its prefix containing the key.
	 * @param destination a buffer with enough remaining space to hold a tuple.
	 * @return true if a tuple with such key was found, false otherwise
	 * @throws SQLPhysicalException
	 */
	public boolean get(ByteBuffer key, ByteBuffer destination) throws SQLPhysicalException;
	
	/**
	 * Given a ByteBuffer with the position at the beginning of a tuple key, writes out the next tuple - in the ordered imposed by the table instance - to the destination,
	 * provided that such a tuple exists.
	 * @param key a buffer with position at the beginning of a tuple key. The contents are to be interpreted by the table schema, that is its prefix containing the key.
	 * @param destination a buffer with enough remaining space to hold a tuple.
	 * @return true if there is a successor and destination was written to, false otherwise
	 * @throws SQLPhysicalException
	 */
	public boolean succ(ByteBuffer value, ByteBuffer destination) throws SQLPhysicalException;
	
	/**
	 * Writes out the first tuple - in the order imposed by the table instance - to the destination, provided that such a tuple exists
	 * @param destination a buffer with enough remaining space to hold a tuple.
	 * @return false if the table is empty, true otherwise
	 * @throws SQLPhysicalException
	 */
	public boolean min(ByteBuffer destination) throws SQLPhysicalException;
	
	/**
	 * Given a ByteBuffer with the position at the beginning of a tuple key, removes the tuple associated with this key from the table, if such a tuple exists.
	 * @param key a buffer with position at the beginning of a tuple key. The contents are to be interpreted by the table schema, that is its prefix containing the key.
	 * @return true, if a tuple with this key was deleted, false otherwise
	 * @throws SQLPhysicalException
	 */
	public boolean delete(ByteBuffer key) throws SQLPhysicalException;
	
	/**
	 * Given a ByteBuffer with the position at the beginning of a tuple, inserts the tuple into the table
	 * @param value a buffer with position at the beginning of a tuple. The contents are to be interpreted by the table schema.
	 * @return true if the value was inserted, false otherwise
	 * @throws SQLPhysicalException
	 */
	public boolean insert(ByteBuffer value) throws SQLPhysicalException;
	
	//public boolean update(ByteBuffer oldKey, ByteBuffer newValue);
}
