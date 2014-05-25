package ch.ethz.inf.dbproject.sqlRevisited.Codegen;

import java.nio.ByteBuffer;

import ch.ethz.inf.dbproject.sqlRevisited.SQLPhysicalException;
import ch.ethz.inf.dbproject.sqlRevisited.SQLType;
import ch.ethz.inf.dbproject.sqlRevisited.Serializer;
import ch.ethz.inf.dbproject.sqlRevisited.TableSchema;

abstract class SQLOperatorAggregate extends SQLOperatorUnary implements SQLOperatorGrouping {

	public SQLOperatorAggregate(TableSchema schema, SQLOperator child) {
		super(schema, child);
	}
	
	@Override
	public int getGroup() {
		return getChild().getGroup();
	}
	
}
