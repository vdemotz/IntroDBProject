package ch.ethz.inf.dbproject.sqlRevisited.Codegen;

import ch.ethz.inf.dbproject.sqlRevisited.TableSchema;

public abstract class SQLOperatorUnary extends SQLOperator{

	
	public SQLOperatorUnary(TableSchema schema, SQLOperator child)
	{
		super(schema, child);
	}
	
	/**
	 * @return the child of this operator
	 */
	protected SQLOperator getChild()
	{
		return children.get(0);
	}
	
}
