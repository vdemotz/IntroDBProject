package ch.ethz.inf.dbproject.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import ch.ethz.inf.dbproject.model.CaseDetail;
import ch.ethz.inf.dbproject.model.CaseNote;
import ch.ethz.inf.dbproject.model.ModelObject;
import ch.ethz.inf.dbproject.model.Person;


public class CaseDatastore implements CaseDatastoreInterface {

	private Connection sqlConnection;

	//template: particular case
	String caseForIdQuery = "select * from CaseDetail where caseId = ?";
	//template: all cases
	String allCasesQuery = "select * from CaseDetail";
	//template: all open or closed cases
	String openCasesQuery = "select * from CaseDetail where isOpen = ? order by date desc";
	//template: all notes for a specific case
	String caseNotesForCaseQuery = "select * from CaseNote where caseId = ?";
	//template: all recent cases
	String recentCasesQuery = "select * from CaseDetail order by date desc";
	//template: oldest unresolved cases
	String oldestUnresolvedCasesQuery = "select * from CaseDetail where isOpen = true order by date asc";
	//template: cases for a specific category
	String casesForCategoryQuery = "select distinct CaseDetail.* from CaseDetail, CategoryForCase where categoryName = ?";
	//template: cases for a specific date
	String casesForDateQuery = "select * from CaseDetail where date = ?";
	//template: suspected persons for a specific case
	String suspectsForCaseQuery = "select distinct person.*" +
								  "from Person person, Suspected suspected, CaseDetail caseDetail" +
				                  "where caseDetail.caseId = ? and suspected.caseId = caseDetail.caseId and suspected.personId = person.personId";
	//template: convicted persons for a specific case
	String convictsForCaseQuery = "select distinct person.*" +
								  "from Person person, Convicted convicted, CaseDetail caseDetail" +
								  "where caseDetail.caseId = ? and convicted.caseId = caseDetail.caseId and convicted.personId = person.personId;";
	
	PreparedStatement caseForIdStatement;
	PreparedStatement allCasesStatement;
	PreparedStatement openCasesStatement;
	PreparedStatement caseNotesForCaseStatement;
	PreparedStatement oldestUnresolvedCasesStatement;
	PreparedStatement recentCasesStatement;
	PreparedStatement casesForCategoryStatement;
	PreparedStatement casesForDateStatement;
	PreparedStatement suspectsForCaseStatement;
	PreparedStatement convictsForCaseStatement;
	
	public CaseDatastore() {
		this.sqlConnection = MySQLConnection.getInstance().getConnection();
		try {
			prepareStatements();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void prepareStatements() throws SQLException
	{
		caseForIdStatement = sqlConnection.prepareStatement(caseForIdQuery);
		allCasesStatement = sqlConnection.prepareStatement(allCasesQuery);
		openCasesStatement = sqlConnection.prepareStatement(openCasesQuery);
		caseNotesForCaseStatement = sqlConnection.prepareStatement(caseNotesForCaseQuery);
		recentCasesStatement = sqlConnection.prepareStatement(recentCasesQuery);
		oldestUnresolvedCasesStatement = sqlConnection.prepareStatement(oldestUnresolvedCasesQuery);
		casesForCategoryStatement = sqlConnection.prepareStatement(casesForCategoryQuery);
		casesForDateStatement = sqlConnection.prepareStatement(casesForDateQuery);
		suspectsForCaseStatement = sqlConnection.prepareStatement(suspectsForCaseQuery);
		convictsForCaseStatement = sqlConnection.prepareStatement(convictsForCaseQuery);
	}
	
	////
	//GENERIC STATEMENT EXECUTION
	////
	
	/**
	 * Executes a statement, and tries to instantiate a list of ModelObjects of the specified modelClass using the resultSet from the statement
	 * If the execution of the statement or instantiation raises an SQLException, null is returned.
	 * @param statement the configured statement to execute and get the results of
	 * @return a list of modelObjects representing the result of the execution of the statement
	 */
	private <T extends ModelObject> List<T> getResults(Class<T> modelClass, PreparedStatement statement)
	{
		 try {
			statement.execute();
			return ModelObject.getAllModelObjectsWithClassFromResultSet(modelClass, statement.getResultSet());
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/////
	//QUERY
	/////
	
	/////
	//Result of type CaseDetail
	/////
	
	@Override
	public CaseDetail getCaseForId(int id) {
		try {
			caseForIdStatement.setInt(1, id);
			caseForIdStatement.execute();
			caseForIdStatement.getResultSet().next();
			return new CaseDetail(caseForIdStatement.getResultSet());
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/////
	//Result of type List<CaseDetail>
	/////
	
	@Override
	public List<CaseDetail> getAllCases() {
		return getResults(CaseDetail.class, allCasesStatement);
	}

	@Override
	public List<CaseDetail> getOpenCases() {
		try {
			openCasesStatement.setBoolean(1, true);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return getResults(CaseDetail.class, openCasesStatement);
	}

	@Override
	public List<CaseDetail> getOldestUnresolvedCases() {
		return getResults(CaseDetail.class, oldestUnresolvedCasesStatement);
	}

	@Override
	public List<CaseDetail> getClosedCases() {
		try {
			openCasesStatement.setBoolean(1, false);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return getResults(CaseDetail.class, openCasesStatement);
	}

	@Override
	public List<CaseDetail> getRecentCases() {
		return getResults(CaseDetail.class, recentCasesStatement);
	}
	
	@Override
	public List<CaseDetail> getCasesForCategory(String name) {
		try {
			casesForCategoryStatement.setString(1, name);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return getResults(CaseDetail.class, casesForCategoryStatement);
	}
	
	@Override
	public List<CaseDetail> getCasesForDate(java.sql.Date date) {
		try {
			casesForDateStatement.setDate(1, date);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return getResults(CaseDetail.class, casesForDateStatement);
	}
	
	////
	//Result of type List<CaseNote>
	////
	
	@Override
	public List<CaseNote> getCaseNotesForCase(int caseId) {
		try {
			caseNotesForCaseStatement.setInt(1, caseId);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return getResults(CaseNote.class, caseNotesForCaseStatement);
	}
	
	////
	//Result of type List<Person>
	////
	
	@Override
	public List<Person> getSuspectsForCase(int caseId) {
		try {
			suspectsForCaseStatement.setInt(1, caseId);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return getResults(Person.class, suspectsForCaseStatement);
	}

	@Override
	public List<Person> getConvictsForCase(int caseId) {
		try {
			suspectsForCaseStatement.setInt(1, caseId);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return getResults(Person.class, convictsForCaseStatement);
	}
	
	////
	//MODIFY
	////
	
	@Override
	public CaseNote addCaseNote(int caseId, String text, String authorUsername) {
		// TODO Auto-generated method stub
		return null;
	}

}
