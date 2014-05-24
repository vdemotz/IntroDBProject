package ch.ethz.inf.dbproject.sqlRevisited.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ch.ethz.inf.dbproject.sqlRevisited.Connection;
import ch.ethz.inf.dbproject.sqlRevisited.PreparedStatement;
import ch.ethz.inf.dbproject.sqlRevisited.SQLException;

public class SQLUpdatePreparedStatementTest {

	private static final String updateCaseDetailisOpenQuery = "update CaseDetail set isOpen=? where caseId=?";
	
	@Test
	public void testUpdatePreparedStatement() throws SQLException{
		Connection connection = Connection.getConnection();
		PreparedStatement updateCaseDetailisOpenStmt = connection.prepareStatement(updateCaseDetailisOpenQuery);
		updateCaseDetailisOpenStmt.setBoolean(1, true);
		updateCaseDetailisOpenStmt.setInt(2, 8);
		updateCaseDetailisOpenStmt.execute();
		assertTrue(updateCaseDetailisOpenStmt.getUpdateCount() > 0);
		
	}
}
