package ch.ethz.inf.dbproject.sqlRevisited.Codegen;

import java.nio.ByteBuffer;

import ch.ethz.inf.dbproject.sqlRevisited.TableConnection;
import ch.ethz.inf.dbproject.sqlRevisited.TableSchema;

public class SQLOperatorBase extends SQLOperator {

	TableConnection physicalTable;
	
	public SQLOperatorBase(TableSchema schema, TableConnection physicalTable) {
		super(schema);
		this.physicalTable = physicalTable;
	}

	@Override
	boolean hasNext() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	void getNext(ByteBuffer resultBuffer) {
		// TODO Auto-generated method stub
	}
	
}
