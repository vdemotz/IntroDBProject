package ch.ethz.inf.dbproject.sqlRevisited;

public abstract class SQLOperatorUnary implements SQLOperator{

	private SQLOperator child;
	
	/**
	 * initializes the operator to be the parent of another operator.
	 * @throws IllegalArgumentException if child is null
	 * @param child
	 */
	public void setChild(SQLOperator child) {
		if (child == null) throw new IllegalArgumentException();
		this.child = child;
	}
	
	/**
	 * @return the child of this operator, or null if it has not been set
	 */
	protected SQLOperator getChild()
	{
		return child;
	}
	
	@Override
	public void rewind() {
		child.rewind();
		internalRewind();
	}
	
	/**
	 * Subclasses can override this method to perform their local rewinding
	 * It is called just after the rewind call to the child
	 */
	protected void internalRewind()
	{
	}
	
}
