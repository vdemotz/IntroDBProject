package ch.ethz.inf.dbproject.sqlRevisited;

import java.nio.ByteBuffer;

public class TableIterator {

	/*
	 * This class represents an iterator to scan a table
	 */
	
	/**
	 * Create new iterator
	 */
	public TableIterator(){
		
	}
	
	/**
	 * Write into destination the current entry of the iterator
	 * @param destination a ByteBuffer to write the entry
	 * @return true if write succeed, false otherwise
	 * @throws SQLPhysicalException
	 */
	public boolean current(ByteBuffer destination) throws SQLPhysicalException{
		return false;
	}
	
	/**
	 * Write in destination the current entry of the iterator and increment cursor
	 * @param destination a ByteBuffer to write the entry
	 * @return true if write succeed, false otherwise
	 */
	public boolean next(ByteBuffer destination) throws SQLPhysicalException{
		return false;
	}
	
	/**
	 * Go to the first entry of the iterator
	 * @return the Iterator itself
	 * @throws SQLPhysicalException
	 */
	public TableIterator first() throws SQLPhysicalException{
		return this;
	}
	
	/**
	 * return to the last entry of the iterator
	 * @return the Iterator itself
	 * @throws SQLPhysicaException
	 */
	public TableIterator last() throws SQLPhysicalException{
		return this;
	}
	
	/**
	 * Write in destination the current entry of the iterator and decrement cursor
	 * @param destination a ByteBuffer to write the entry
	 * @return true if write succeed, false otherwise
	 */
	public boolean previous(ByteBuffer destination) throws SQLPhysicalException{
		return false;
	}
	
	

}
