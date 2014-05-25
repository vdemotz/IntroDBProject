package ch.ethz.inf.dbproject.sqlRevisited.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ch.ethz.inf.dbproject.sqlRevisited.Connection;
import ch.ethz.inf.dbproject.sqlRevisited.PreparedStatement;
import ch.ethz.inf.dbproject.sqlRevisited.SQLException;

public class SQLDeletePreparedStatementTest {

	private static final String deleteFromPersonQuery = "delete from Person where personId=?";
	
	@Test
	public void testDeletePreparedStatement() throws SQLException{
		Connection connection = Connection.getConnection();
		PreparedStatement deleteFromPersonStmt = connection.prepareStatement(deleteFromPersonQuery);
		deleteFromPersonStmt.setInt(1, 2);
		deleteFromPersonStmt.execute();
		assertTrue(deleteFromPersonStmt.getUpdateCount() > 0);
		
	}
}
