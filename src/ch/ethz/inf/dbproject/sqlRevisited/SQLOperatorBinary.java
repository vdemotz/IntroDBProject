package ch.ethz.inf.dbproject.sqlRevisited;

public abstract class SQLOperatorBinary implements SQLOperator{
	
	private SQLOperator leftChild;
	private SQLOperator rightChild;
	
	/**
	 * initializes the operator to be the parent of two other operators
	 * @throws IllegalArgumentException if left or right is null
	 * @param a the left child
	 * @param b the right child
	 */
	public void setChildren(SQLOperator left, SQLOperator right) {
		if (left == null) throw new IllegalArgumentException();
		if (right == null) throw new IllegalArgumentException();
		leftChild = left;
		rightChild = right;
	}
	
	/**
	 * @return the leftChild of this operator, or null if it has not been set
	 */
	protected SQLOperator getLeftChild() {
		return leftChild;
	}
	
	/**
	 * @return the rightChild of this operator, or null if it has not been set
	 */
	protected SQLOperator getRightChild() {
		return rightChild;
	}

	@Override
	public void rewind() {
		leftChild.rewind();
		rightChild.rewind();
		internalRewind();
	}
	
	/**
	 * Subclasses can override this method to perform their local rewinding
	 * It is called just after the rewind call to the children
	 */
	protected void internalRewind()
	{
	}
	
}
