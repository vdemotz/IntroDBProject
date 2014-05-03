package ch.ethz.inf.dbproject.sqlRevisited;

import java.nio.*;

/**
 * The results of an SQLOperator is a list of tuples. The SQLOperator provides an iterator interface over these tuples.
 * All tuples share some array of types, an array of attribute names and attribute sizes. Each tuple is represented as a sequence of characters.
 * This means that the size of an attribute of different tuples may not vary (for now).
 */
public interface SQLOperator {
	
	/**
	 * @return true if there is another result, false otherwise
	 */
	boolean hasNext();
	
	/**
	 * Advance the iterator to the next position and write the next tuple into the resultBuffer.
	 * The tuple is written at the current position of the buffer and the position is incremented so that it points just after the tuple that has been written
	 * @param resultBuffer
	 * @precondition hasNext()==true
	 * @result the sizes of the attributes
	 */
	void getNext(CharBuffer resultBuffer);
	
	/**
	 * Resets the iterator to point just before the first tuple.
	 */
	void rewind();
	
	/**
	 * @return the sum of attribute sizes
	 */
	int getTupleSize();
	
	/**
	 * @return an array of number of characters per attribute
	 */
	int[] getAttributeSizes();
	
	/**
	 * @return an array of types of the resulting attributes.
	 */
	SQLType[] getAttributeTypes();
	
	/**
	 * @return an array of names for the resulting attributes
	 */
	String[] getAttributeNames();
	
	/**
	 * The result is always the same as the length of any of the attribute arrays
	 * @return the number of attributes the results have
	 */
	int getNumberOfAttributes();
}