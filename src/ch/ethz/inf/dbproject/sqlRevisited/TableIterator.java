package ch.ethz.inf.dbproject.sqlRevisited;

import java.nio.ByteBuffer;

public interface TableIterator {

	/*
	 * This class represents an iterator to scan a table
	 */
	
	/**
	 * If there is a next entry, write it to the destination, advance the cursor and return true.
	 * Otherwise, do not modify the destination and return false.
	 * @param destination a ByteBuffer to write the entry, holding enough space as defined by the method that returned the iterator.
	 * @return true if an entry was written, false otherwise
	 */
	public boolean next(ByteBuffer destination) throws SQLPhysicalException;
	
}
