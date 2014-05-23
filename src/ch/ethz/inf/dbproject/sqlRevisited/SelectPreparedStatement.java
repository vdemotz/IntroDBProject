package ch.ethz.inf.dbproject.sqlRevisited;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.ethz.inf.dbproject.sqlRevisited.Codegen.SQLCodegen;
import ch.ethz.inf.dbproject.sqlRevisited.Codegen.SQLOperator;
import java.util.concurrent.locks.Lock;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.ParsedQuery;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.SyntaxTreeNode;

public class SelectPreparedStatement  extends AbstractPreparedStatement {

	private final List<PhysicalTableInterface> tables;
	private static final SQLCodegen codegen = new SQLCodegen();
	private final SyntaxTreeNode syntaxTree;
	
	/**
	 * Create a new PreparedStatement query
	 * @param db 
	 * @param pq ParsedQuery of type SELECT
	 * @param l a Read Lock
	 * @param db Database to acquire connection to tables
	 */
	public SelectPreparedStatement(ParsedQuery pq, Lock l, List<PhysicalTableInterface> tables){
		syntaxTree = pq.getSyntaxTreeDynamicNode().dynamicChildren.get(0);
		this.tables = tables;
	}

	@Override
	public ResultSet executeQuery() throws SQLException {
		//TODO acquire lock
		SQLOperator operator = codegen.generateSelectStatement(syntaxTree, tables, args);
		ArrayList<byte[]> results = new ArrayList<byte[]>();
		ByteBuffer resultBuffer = ByteBuffer.wrap(new byte[operator.schema.getSizeOfEntry()]);
		while (operator.next(resultBuffer)) {
			results.add(Arrays.copyOf(resultBuffer.array(), resultBuffer.array().length));
			resultBuffer.rewind();
		}
		//TODO release lock
		return new ResultSet(operator.schema, results);
	}

	@Override
	public boolean execute() throws SQLException {
		executeQuery();
		return false;
	}

	@Override
	public int getUpdateCount() {
		return 0;
	}
}
