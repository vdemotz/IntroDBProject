package ch.ethz.inf.dbproject.sqlRevisited.Codegen;

import java.nio.ByteBuffer;

import ch.ethz.inf.dbproject.sqlRevisited.PhysicalTableInterface;
import ch.ethz.inf.dbproject.sqlRevisited.SQLPhysicalException;
import ch.ethz.inf.dbproject.sqlRevisited.TableConnection;
import ch.ethz.inf.dbproject.sqlRevisited.TableIterator;
import ch.ethz.inf.dbproject.sqlRevisited.TableSchema;

public class SQLOperatorBase extends SQLOperator {

	private final PhysicalTableInterface physicalTable;
	private TableIterator iterator;
	private boolean hasNext;
	
	public SQLOperatorBase(TableSchema schema, PhysicalTableInterface physicalTable) {
		super(schema);
		this.physicalTable = physicalTable;
	}
	
	@Override
	protected void internalOpen() throws SQLPhysicalException {
		internalRewind();
	}

	@Override
	public boolean next(ByteBuffer resultBuffer) throws SQLPhysicalException {
		return iterator.next(resultBuffer);
	}
	
	protected void internalRewind() throws SQLPhysicalException {
		iterator = physicalTable.getIteratorFirst();
	}

	@Override
	public SQLOperator copyWithSchema(TableSchema schema) {
		return new SQLOperatorBase(schema, physicalTable);
	}
	
	@Override
	protected String toStringInfo()
	{
		return schema.tableName;
	}
}
