package ch.ethz.inf.dbproject.sqlRevisited.Codegen;

import java.nio.ByteBuffer;

import ch.ethz.inf.dbproject.sqlRevisited.SQLPhysicalException;
import ch.ethz.inf.dbproject.sqlRevisited.TableSchema;

public class SQLOperatorRename extends SQLOperatorUnary {

	public SQLOperatorRename(TableSchema schema, SQLOperator child) {
		super(schema, child);
	}

	@Override
	public boolean next(ByteBuffer resultBuffer) throws SQLPhysicalException {
		return getChild().next(resultBuffer);
	}
	
	@Override
	protected String toStringInfo() {
		return schema.tableName;
	}
}
