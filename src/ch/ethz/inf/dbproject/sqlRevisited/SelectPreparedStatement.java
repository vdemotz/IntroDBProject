package ch.ethz.inf.dbproject.sqlRevisited;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import ch.ethz.inf.dbproject.sqlRevisited.Codegen.SQLCodegen;
import ch.ethz.inf.dbproject.sqlRevisited.Codegen.SQLOperator;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.ParsedQuery;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.SQLSemanticException;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.SyntaxTreeNode;

public class SelectPreparedStatement  extends AbstractPreparedStatement {

	private final List<PhysicalTableInterface> tables;
	private static final SQLCodegen codegen = new SQLCodegen();
	private final SyntaxTreeNode syntaxTree;
	private ResultSet lastResult = null;
	
	/**
	 * Create a new PreparedStatement query
	 * @param db 
	 * @param pq ParsedQuery of type SELECT
	 * @param l a Read Lock
	 * @param db Database to acquire connection to tables
	 * @throws SQLSemanticException 
	 */
	public SelectPreparedStatement(ParsedQuery pq, Lock l, List<PhysicalTableInterface> tables) throws SQLSemanticException {
		super(l);
		List<TableSchema> schemata = new ArrayList<TableSchema>(tables.size());
		for (PhysicalTableInterface table : tables) {
			schemata.add(table.getTableSchema());
		}
		syntaxTree = pq.getSyntaxTreeDynamicNode().dynamicChildren.get(0).instanciateWithSchemata(schemata).rewrite();
		this.typeArgs = syntaxTree.schema.getAttributesTypes();
		this.args = new Object[8];//TODO : properly count number of arguments!
		this.tables = tables;
	}

	@Override
	public ResultSet executeQuery() throws SQLException {
		//acquire lock
		lock.lock();
		try {
			SQLOperator operator = codegen.generateSelectStatement(syntaxTree, tables, args);
			ArrayList<byte[]> results = new ArrayList<byte[]>();
			ByteBuffer resultBuffer = ByteBuffer.wrap(new byte[operator.schema.getSizeOfEntry()]);
			operator.open();
			while (operator.next(resultBuffer)) {
				results.add(Arrays.copyOf(resultBuffer.array(), resultBuffer.array().length));
				resultBuffer.rewind();
			}
			lastResult =  new ResultSet(operator.schema, results);
		} finally {
			//release lock
			lock.unlock();
		}
		return lastResult;
	}

	@Override
	public boolean execute() throws SQLException {
		executeQuery();
		return true;
	}
	
	@Override
	public ResultSet getResultSet() {
		return lastResult;
	}

	@Override
	public int getUpdateCount() {
		return 0;
	}
}
