package ch.ethz.inf.dbproject.sqlRevisited;

import java.nio.CharBuffer;

import java.util.Arrays;

public class SQLOperatorCross  extends SQLOperatorBinary {

	final SQLType[] attributeTypes;
	final String[] attributeNames;
	final int[] attributeSizes;
	final int numberOfAttributes;
	final int tupleSize;
	final CharBuffer currentLefthandTuple;
	
	public SQLOperatorCross(SQLOperator left, SQLOperator right) {
		setChildren(left, right);
		
		numberOfAttributes = left.getNumberOfAttributes()+right.getNumberOfAttributes();
		
		attributeTypes = concatArrays(left.getAttributeTypes(), right.getAttributeTypes());
		attributeNames = concatArrays(left.getAttributeNames(), right.getAttributeNames());
		attributeSizes = concatArrays(left.getAttributeSizes(), right.getAttributeSizes());
		
		tupleSize = left.getTupleSize()+right.getTupleSize();
		
		if (left.hasNext()) {
			currentLefthandTuple = CharBuffer.wrap(new char[getLeftChild().getTupleSize()]);
		} else {
			currentLefthandTuple = null;//indicates the left relation is empty
		}
		
		open();
	}
	
	////
	//SQLOperator Interface
	////
	
	@Override
	public boolean hasNext() {
		//if the left relation is not exhausted, there are more results
		//if the left relation is exhausted, then there are results as long as the right relation is not exhausted and the left relation was not empty
		return getLeftChild().hasNext() || (currentLefthandTuple != null && getRightChild().hasNext());
	}

	@Override
	public void getNext(CharBuffer resultBuffer) {
		assert (hasNext());
		
		if (!getRightChild().hasNext()) {//if the right relation is exhausted, go to the next left tuple and rewind the right relation
			getLeftChild().getNext(currentLefthandTuple);
			getRightChild().rewind();
		}
		
		resultBuffer.put(currentLefthandTuple.array());
		getRightChild().getNext(resultBuffer);
	}

	@Override
	public SQLType[] getAttributeTypes() {
		return attributeTypes;
	}

	@Override
	public String[] getAttributeNames() {
		return attributeNames;
	}

	@Override
	public int getNumberOfAttributes() {
		return numberOfAttributes;
	}

	@Override
	public int[] getAttributeSizes() {
		return attributeSizes;
	}
	
	@Override
	public int getTupleSize() {
		return tupleSize;
	}
	
	////
	//INTERNAL
	////
	
	@Override
	protected void internalRewind()
	{
		open();
	}
	
	private void open() {
		if (getLeftChild().hasNext()) {
			getLeftChild().getNext(currentLefthandTuple);
		}
	}
	
	////
	//HELPERS
	////
	
	private static <T> T[] concatArrays(T[] left, T[] right)
	{
		T[] result = Arrays.copyOf(left, left.length+right.length);
		for (int i=left.length; i < left.length+right.length; i++) {
			result[i] = right[i];
		}
		return result;
	}
	
	private int[] concatArrays(int[] left, int[] right) {
		int[] result = Arrays.copyOf(left, left.length+right.length);
		for (int i=left.length; i < left.length+right.length; i++) {
			result[i] = right[i];
		}
		return result;
	}

}
