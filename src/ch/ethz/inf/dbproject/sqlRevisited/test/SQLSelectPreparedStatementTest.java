package ch.ethz.inf.dbproject.sqlRevisited.test;

import static org.junit.Assert.*;

import org.junit.Test;
import ch.ethz.inf.dbproject.sqlRevisited.Connection;
import ch.ethz.inf.dbproject.sqlRevisited.ResultSet;
import ch.ethz.inf.dbproject.sqlRevisited.SQLException;
import ch.ethz.inf.dbproject.sqlRevisited.PreparedStatement;

public class SQLSelectPreparedStatementTest {

	@Test
	public void test() {
		
		Connection connection = null;
		try {
			connection = Connection.getConnection();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			PreparedStatement statement = connection.prepareStatement("select * from User");
			ResultSet rs = statement.executeQuery();
			
			while (rs.next()) {
				System.out.println(rs.getObject("username"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			fail("unexpected SQLException");
		}
		
	}

}
