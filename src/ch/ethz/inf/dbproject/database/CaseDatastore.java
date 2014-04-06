package ch.ethz.inf.dbproject.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.ethz.inf.dbproject.model.CaseDetail;
import ch.ethz.inf.dbproject.model.CaseNote;
import ch.ethz.inf.dbproject.model.Category;
import ch.ethz.inf.dbproject.model.CategorySummary;
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
	String caseNotesForCaseQuery = "select * from CaseNote where caseId = ? order by caseNoteId desc";
	//template: all recent cases
	String recentCasesQuery = "select * from CaseDetail order by date desc";
	//template: oldest unresolved cases
	String oldestUnresolvedCasesQuery = "select * from CaseDetail where isOpen = true order by date asc";
	//template: cases for a specific category
	String casesForCategoryQuery = "select CaseDetail.* from CaseDetail caseDetail, CategoryForCase categoryForCase where categoryName = ? and caseDetail.caseId = categoryForCase.caseId";
	//template: cases for a specific date
	String casesForDateQuery = "select * from CaseDetail where Date(date) = ?";
	//template: cases for an approximate date
	String casesForDateLikeQuery = "select * from CaseDetail where date like ?";
	//template: suspected persons for a specific case
	String suspectsForCaseQuery = "select person.* " +
								  "from Person person, Suspected suspected, CaseDetail caseDetail " +
				                  "where caseDetail.caseId = ? and suspected.caseId = caseDetail.caseId and suspected.personId = person.personId";
	//template: convicted persons for a specific case
	String convictsForCaseQuery = "select person.* " +
								  "from Person person, Convicted convicted, CaseDetail caseDetail " +
								  "where caseDetail.caseId = ? and convicted.caseId = caseDetail.caseId and convicted.personId = person.personId";
	//template: all categories for a specific case
	String categoriesForCaseQuery = "select Category.* " +
									"from CaseDetail caseDetail, CategoryForCase categoryForCase, Category category " +
								    "where caseDetail.caseId = ? and categoryForCase.caseId = caseDetail.caseId and categoryForCase.categoryName = category.Name";
	//template: category summary
	String categorySummaryQuery = "select categoryName, count(*) as numberOfCases from CategoryForCase group by categoryName";
	//template: get the next id for the case note of a particular case
	String nextCaseNoteIdForCaseQuery = "select coalesce (max(caseNoteId)+1, 1) from CaseNote where caseId=?";//if there is no caseNote yet, max returns null, coalesce selects the first non-null argument
	//template add a new note
	String insertIntoCaseNoteQuery = "insert into CaseNote " +
								"values (?, " +//caseId
								"?, " +//caseNoteId
								"?, " +//text
								"?, " +//date
								"?)";//authorUsername
	//template set case is open
	String updateCaseIsOpenQuery = "update CaseDetail set isOpen = ? where caseId = ?";
<<<<<<< HEAD
	//template add Suspec
	String addSuspectQuery = "insert into Suspected values (?, ?)";
	//template add Convicts
	String addConvictQuery = "insert into Convicted values (?, ?, ?)";
=======
	//template add new case
	String insertIntoCaseDetailQuery = "insert into CaseDetail (caseId, title, street, city, zipCode, isOpen, date, description, authorName) " +
							"values(?, ?, ?, ?, ? ,? ,? ,? ,?)";
	//template get the next id for the case detail
	String nextCaseDetailIdQuery = "select max(caseId) from CaseDetail";
>>>>>>> b42f844e737920e39cd29270d1244c10e7117c34
	
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
	PreparedStatement categoriesForCaseStatement;
	PreparedStatement categorySummaryStatement;
	PreparedStatement casesForDateLikeStatement;
	PreparedStatement nextCaseNoteIdForCaseStatement;
	PreparedStatement insertIntoCaseNoteStatement;
	PreparedStatement updateCaseIsOpenStatement;
<<<<<<< HEAD
	PreparedStatement addSuspectStatement;
	PreparedStatement addConvictStatement;
=======
	PreparedStatement insertIntoCaseDetailStatement;
	PreparedStatement nextCaseDetailIdStatement;
>>>>>>> b42f844e737920e39cd29270d1244c10e7117c34
	
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
		categoriesForCaseStatement = sqlConnection.prepareStatement(categoriesForCaseQuery);
		categorySummaryStatement = sqlConnection.prepareStatement(categorySummaryQuery);
		casesForDateLikeStatement = sqlConnection.prepareStatement(casesForDateLikeQuery);
		nextCaseNoteIdForCaseStatement = sqlConnection.prepareStatement(nextCaseNoteIdForCaseQuery);
		insertIntoCaseNoteStatement = sqlConnection.prepareStatement(insertIntoCaseNoteQuery);
		updateCaseIsOpenStatement = sqlConnection.prepareStatement(updateCaseIsOpenQuery);
<<<<<<< HEAD
		addSuspectStatement =  sqlConnection.prepareStatement(addSuspectQuery);
		addConvictStatement = sqlConnection.prepareStatement(addConvictQuery);
=======
		insertIntoCaseDetailStatement = sqlConnection.prepareStatement(insertIntoCaseDetailQuery);
		nextCaseDetailIdStatement = sqlConnection.prepareStatement(nextCaseDetailIdQuery);
>>>>>>> b42f844e737920e39cd29270d1244c10e7117c34
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
			setCaseId(caseForIdStatement, id);
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
		return getCasesWithIsOpen(true);
	}

	@Override
	public List<CaseDetail> getOldestUnresolvedCases() {
		return getResults(CaseDetail.class, oldestUnresolvedCasesStatement);
	}

	@Override
	public List<CaseDetail> getClosedCases() {
		return getCasesWithIsOpen(false);
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
			return null;
		}
		return getResults(CaseDetail.class, casesForDateStatement);
	}
	
	@Override
	public List<CaseDetail> getCasesForDateLike(String date) {
		try {
			casesForDateLikeStatement.setString(1, date + "%");
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return getResults(CaseDetail.class, casesForDateLikeStatement);
	}
	
	////
	//Result of type List<CaseNote>
	////
	
	@Override
	public List<CaseNote> getCaseNotesForCase(int caseId) {
		setCaseId(caseNotesForCaseStatement, caseId);
		return getResults(CaseNote.class, caseNotesForCaseStatement);
	}
	
	////
	//Result of type List<Person>
	////
	
	@Override
	public List<Person> getSuspectsForCase(int caseId) {
		setCaseId(suspectsForCaseStatement, caseId);
		return getResults(Person.class, suspectsForCaseStatement);
	}

	@Override
	public List<Person> getConvictsForCase(int caseId) {
		setCaseId(convictsForCaseStatement, caseId);
		return getResults(Person.class, convictsForCaseStatement);
	}
	
	/////
	//Result of type List<Category>
	/////
	
	@Override
	public List<Category> getCategoriesForCase(int caseId) {
		setCaseId(categoriesForCaseStatement, caseId);
		return getResults(Category.class, categoriesForCaseStatement);
	}
	
	////
	//Result of type List<CategorySummary>
	////
	
	@Override
	public List<CategorySummary> getCategorySummary() {
		return getResults(CategorySummary.class, categorySummaryStatement);
	}
	
	/////
	//Helper Queries
	/////
	
	/**
	 * @param statement a statement whose first parameter is a caseId
	 * @param caseId the value that the caseId argument will be set to
	 */
	private void setCaseId(PreparedStatement statement, int caseId)
	{
		try {
			statement.setInt(1, caseId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * if isOpen = true, same semantic as getOpenCases, otherwise same as getClosedCases
	 */
	private List<CaseDetail> getCasesWithIsOpen(boolean isOpen) {
		try {
			openCasesStatement.setBoolean(1, isOpen);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return getResults(CaseDetail.class, openCasesStatement);
	}
	
	private int getNextCaseNoteIdForCase(int caseId)
	{
		try {
			nextCaseNoteIdForCaseStatement.setInt(1, caseId);
			nextCaseNoteIdForCaseStatement.execute();
			nextCaseNoteIdForCaseStatement.getResultSet().next();
			return nextCaseNoteIdForCaseStatement.getResultSet().getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	private int getNextCaseDetailId()
	{
		try {
			ResultSet rs = nextCaseDetailIdStatement.executeQuery();
			if (!rs.first()){ return 0; }
			return (rs.getInt("max(personId)")+1);
		} catch (SQLException ex){
			ex.printStackTrace();
			return -1;
		}
	}
	
	////
	//MODIFY
	////
	
	@Override
	public CaseNote insertIntoCaseNote(int caseId, String text, String authorUsername) {
		synchronized (this.getClass()) {//TODO: it would be nice if this could be nested into the insert query (then no lock would be necessary here)
			int caseNoteId = getNextCaseNoteIdForCase(caseId);
			if (caseNoteId == -1) return null;
			java.util.Date date = new java.util.Date();
			java.sql.Timestamp datesql = new java.sql.Timestamp(date.getTime());
			try {
				insertIntoCaseNoteStatement.setInt(1, caseId);
				insertIntoCaseNoteStatement.setInt(2, caseNoteId);
				insertIntoCaseNoteStatement.setString(3, text);
				insertIntoCaseNoteStatement.setObject(4, datesql);
				insertIntoCaseNoteStatement.setString(5, authorUsername);
				insertIntoCaseNoteStatement.execute();
				return new CaseNote(caseId, caseNoteId, text, datesql, authorUsername);
			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	@Override
	public boolean updateCaseIsOpen(int caseId, boolean isOpen) {
		try {
			updateCaseIsOpenStatement.setBoolean(1, isOpen);
			updateCaseIsOpenStatement.setInt(2, caseId);
			updateCaseIsOpenStatement.execute();
			return updateCaseIsOpenStatement.getUpdateCount() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public CaseDetail insertIntoCaseDetail(String title, String city, String zipCode, String street, Timestamp date, String description, String authorUsername) {
		synchronized (this.getClass()){
			try {
				int caseId = getNextCaseDetailId();
				insertIntoCaseDetailStatement.setInt(1, caseId);
				insertIntoCaseDetailStatement.setString(2, title);
				insertIntoCaseDetailStatement.setString(3, street);
				insertIntoCaseDetailStatement.setString(4, city);
				insertIntoCaseDetailStatement.setString(5, zipCode);
				insertIntoCaseDetailStatement.setBoolean(6, true);
				insertIntoCaseDetailStatement.setString(7, date.toString());
				insertIntoCaseDetailStatement.setString(8, description);
				insertIntoCaseDetailStatement.setString(9, authorUsername);
				insertIntoCaseDetailStatement.execute();
				return new CaseDetail(caseId, title, city, street, zipCode, true, date, description, authorUsername);
			} catch (SQLException ex){
				ex.printStackTrace();
				return null;
			}
		}
	}
<<<<<<< HEAD

	@Override
	public boolean addSuspectToCase(int caseId, int personId) {
		try {
			addSuspectStatement.setInt(1, personId);
			addSuspectStatement.setInt(2, caseId);
			addSuspectStatement.execute();
			return addSuspectStatement.getUpdateCount() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean addConvictToCase(int caseId, int personId, int convictionId) {
		// TODO Auto-generated method stub
		return false;
	}



=======
>>>>>>> b42f844e737920e39cd29270d1244c10e7117c34
}
