package ch.ethz.inf.dbproject.sqlRevisited;

import java.util.Arrays;

public class Demonstration {

	
	private static final String suspectsForCaseQuery = "select person.* " +
			  										   "from Person, Suspected " +
			  										   "where caseId = ? and Suspected.personId = Person.personId";
	
	private static PreparedStatement statement;

	/**
	 * @param args
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException {
		
		Connection connection = new Connection(System.getProperty("user.home")+ "/SQLRevisited");
		
		statement = connection.prepareStatement(suspectsForCaseQuery);
		
		statement.setInt(1, 0);
		
		ResultSet result = statement.executeQuery();
		
		while (result.next()) {
			System.out.println(Arrays.deepToString(result.getObjects()));
		}
		
	}

}
