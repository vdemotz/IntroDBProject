package ch.ethz.inf.dbproject.sqlRevisited.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ch.ethz.inf.dbproject.sqlRevisited.Connection;
import ch.ethz.inf.dbproject.sqlRevisited.Database;
import ch.ethz.inf.dbproject.sqlRevisited.PreparedStatement;
import ch.ethz.inf.dbproject.sqlRevisited.SQLException;

public class SQLInsertPreparedStatementTest {

	private static final String insertIntoCaseNoteQuery = "insert into CaseNote " +
			"values (?, " +//caseId
			"?, " +//caseNoteId
			"?, " +//text
			"?, " +//date
			"?)";//authorUsername
	
	private static final String insertIntoPersonQuery = "insert into Person values(?, ?, ?)";
	
	@Test
	public void testInsertPreparedStatement() throws SQLException{
		Connection connection = Connection.getConnection();
		PreparedStatement insertIntoCaseNoteStmt = connection.prepareStatement(insertIntoPersonQuery);
		insertIntoCaseNoteStmt.setInt(1, 2);
		insertIntoCaseNoteStmt.setString(2, "Jean");
		insertIntoCaseNoteStmt.setString(3, "Raymond");
		insertIntoCaseNoteStmt.execute();
		assertTrue(insertIntoCaseNoteStmt.getUpdateCount() > 0);
		
	}
}
