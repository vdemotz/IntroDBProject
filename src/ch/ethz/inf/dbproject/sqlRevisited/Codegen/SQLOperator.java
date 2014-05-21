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
	public final void open() throws SQLPhysicalException {
		for (SQLOperator child : children) {
			child.open();
		}
		internalOpen();
	}
	
	/**
	 * If there is a next entry, write it to the resultBuffer, go to the next entry and return true.
	 * Otherwise, do not modify the resultBuffer and return false.
	 * @param destination a ByteBuffer to write the entry, holding enough space to hold an entry, whose size is given by the schema.
	 * @return true if an entry was written, false otherwise
	 */
	public abstract boolean next(ByteBuffer resultBuffer) throws SQLPhysicalException;
	
	/**
	 * Resets the iterator to point just before the first tuple.
	 */
	final void rewind() throws SQLPhysicalException {
		for (SQLOperator operator : children) {
			operator.rewind();
		}
		internalRewind();
	}
	
	/**
	 * Subclasses can override this method to perform their local rewinding
	 * It is called just after the rewind call to the children
	 */
	protected void internalRewind() throws SQLPhysicalException {
	}
	
	/**
	 * Subclasses may override this method to perform custom open operations.
	 * This method is called after all children have been opened.
	 */
	protected void internalOpen() throws SQLPhysicalException {
	}
	
	/**
	 * Copies a closed operator, with a new schema.
	 * Behavior is undefined if the operator has already been opened.
	 * @param schema
	 * @return
	 */
	public abstract SQLOperator copyWithSchema(TableSchema schema);
	
	////
	//OVERRIDING OBJECT
	////
	
	@Override
	public String toString()
	{
		String result = this.getClass().getSimpleName().substring("SQLOperator".length());
		String info = toStringInfo();
		if (info != null && info.length() > 0) {
			result = result + " (" + info + ")";
		}
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
	
	protected String toStringInfo()
	{
		return "";
	}
}