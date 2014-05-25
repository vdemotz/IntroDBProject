package ch.ethz.inf.dbproject.sqlRevisited.Codegen;

import ch.ethz.inf.dbproject.sqlRevisited.TableSchema;

abstract class SQLOperatorBinary extends SQLOperator{
	
	public SQLOperatorBinary (TableSchema schema, SQLOperator left, SQLOperator right)
	{
		super(schema, left, right);
	}
	
	/**
	 * @return the leftChild of this operator, or null if it has not been set
	 */
	protected SQLOperator getLeftChild() {
		return children.get(0);
	}
	
	/**
	 * @return the rightChild of this operator, or null if it has not been set
	 */
	protected SQLOperator getRightChild() {
		return children.get(1);
	}
	
	
}
