package ch.ethz.inf.dbproject.sqlRevisited.Codegen;

import java.nio.*;

import ch.ethz.inf.dbproject.sqlRevisited.SQLPhysicalException;
import ch.ethz.inf.dbproject.sqlRevisited.TableSchema;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.ImmutableArray;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.SyntaxTreeNode;

/**
 * The results of an SQLOperator is a list of tuples. The SQLOperator provides an iterator interface over these tuples.
 * All tuples share a TableSchema
 */
public abstract class SQLOperator {
	
	public final TableSchema schema;
	protected final ImmutableArray<SQLOperator> children;
	
	public SQLOperator(TableSchema schema, SQLOperator ... children)
	{
		this.schema = schema;
		this.children = new ImmutableArray<SQLOperator>(children);
	}
	
	/**
	 * Has to be called before first use.
	 * @throws SQLPhysicalException
	 */
	public abstract void open() throws SQLPhysicalException;
	
	/**
	 * @return true if there is another result, false otherwise
	 */
	public abstract boolean hasNext();
	
	/**
	 * Advance the iterator to the next position and write the next tuple into the resultBuffer.
	 * The tuple is written at the current position of the buffer and the position is incremented so that it points just after the tuple that has been written
	 * @param resultBuffer
	 * @precondition hasNext()==true
	 * @result the sizes of the attributes
	 */
	public abstract void getNext(ByteBuffer resultBuffer) throws SQLPhysicalException;
	
	/**
	 * Resets the iterator to point just before the first tuple.
	 */
	void rewind() throws SQLPhysicalException {
		for (SQLOperator operator : children) {
			operator.rewind();
		}
		internalRewind();
	}
	
	/**
	 * Subclasses can override this method to perform their local rewinding
	 * It is called just after the rewind call to the children
	 */
	protected void internalRewind() throws SQLPhysicalException
	{
	}
	
	////
	//OVERRIDING OBJECT
	////
	
	@Override
	public String toString()
	{
		String result = this.getClass().getSimpleName().substring("SQLOperator".length());
		if (children.length > 0) {
			result += "[ ";
			for (SQLOperator child : children) {
				if (child != null) {
					result += child.toString() + " ";
				}
			}
			result += "]";
		}
		return  result;
	}
}