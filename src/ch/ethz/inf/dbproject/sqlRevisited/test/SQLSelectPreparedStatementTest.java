package ch.ethz.inf.dbproject.sqlRevisited.test;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;
import ch.ethz.inf.dbproject.sqlRevisited.Connection;
import ch.ethz.inf.dbproject.sqlRevisited.ResultSet;
import ch.ethz.inf.dbproject.sqlRevisited.SQLException;
import ch.ethz.inf.dbproject.sqlRevisited.PreparedStatement;

public class SQLSelectPreparedStatementTest {

	private static final String suspectsForCaseQuery = "select person.* " +
			  "from Person person, Suspected suspected " +
            "where suspected.caseId = ? and suspected.personId = person.personId";
	
	private static final String getPersonsForConvictionTypeString = "select person.* " +
			   "from Conviction conviction, Person person, CategoryForCase categoryForCase "+
			   "where conviction.personId = person.personId "+
			   "and conviction.caseId = categoryForCase.caseId " +
			   "and categoryForCase.categoryName = ? " +
			   "order by lastName, firstName";
	
	private static final String categoriesForCaseQuery = "select distinct Category.* " +
			"from CaseDetail caseDetail, CategoryForCase categoryForCase, Category category " +
		    "where caseDetail.caseId = ? and categoryForCase.caseId = caseDetail.caseId and categoryForCase.categoryName = category.Name";
	
	//template: all cases
	private static final String allCasesQuery = "select * from CaseDetail";
	//template: all open or closed cases
	private static final String openCasesQuery = "select * from CaseDetail where isOpen = ? order by date desc";
	
	private static final String getAllPersonsString = "select * from Person order by lastName desc, firstName desc";

	private String[] testSelectSucceeds = {suspectsForCaseQuery, getPersonsForConvictionTypeString, categoriesForCaseQuery, getAllPersonsString, allCasesQuery, openCasesQuery, openCasesQuery};
	
	private Object[][] arguments = {{9},{"Suicide"},{10},{}, {}, {true}, {false}};
	
	@Test
	public void test() {
		
		Connection connection = null;
		try {
			connection = Connection.getConnection();
		} catch (SQLException e1) {
			e1.printStackTrace();
			fail("could not establish connection");
		}
		
		for (int i=0; i<testSelectSucceeds.length; i++) {
			try {
				String query = testSelectSucceeds[i];
				System.out.println(query);
				PreparedStatement statement = connection.prepareStatement(query);
				for (int j=0; j<arguments[i].length; j++) {
					statement.setObject(j, arguments[i][j]);
				}
				ResultSet rs = statement.executeQuery();
				
				while (rs.next()) {
					System.out.println(Arrays.deepToString(rs.getObjects()));
				}
			} catch (SQLException e) {
				e.printStackTrace();
				fail("unexpected SQLException");
			}
		}
		
	}

}