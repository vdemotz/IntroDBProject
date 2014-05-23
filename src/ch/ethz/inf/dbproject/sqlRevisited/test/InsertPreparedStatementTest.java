package ch.ethz.inf.dbproject.sqlRevisited.test;

import org.junit.Test;

import ch.ethz.inf.dbproject.sqlRevisited.Connection;
import ch.ethz.inf.dbproject.sqlRevisited.Database;
import ch.ethz.inf.dbproject.sqlRevisited.PreparedStatement;
import ch.ethz.inf.dbproject.sqlRevisited.SQLException;

public class InsertPreparedStatementTest {

	private static final String insertIntoCaseNoteQuery = "insert into CaseNote " +
			"values (?, " +//caseId
			"?, " +//caseNoteId
			"?, " +//text
			"?, " +//date
			"?)";//authorUsername
	
	@Test
	public void testInsertPreparedStatement() throws SQLException{
		Connection connection = Connection.getConnection();
		PreparedStatement insertIntoCaseNoteStmt = connection.prepareStatement(insertIntoCaseNoteQuery);
	}
}
