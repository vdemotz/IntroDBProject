package ch.ethz.inf.dbproject.database;

import java.util.List;
import ch.ethz.inf.dbproject.sqlRevisited.*;
import ch.ethz.inf.dbproject.model.CaseDetail;
import ch.ethz.inf.dbproject.model.CaseNote;
import ch.ethz.inf.dbproject.model.CategorySummary;
import ch.ethz.inf.dbproject.model.ConvictionJoinPerson;
import ch.ethz.inf.dbproject.model.Person;


public class CaseDatastore extends Datastore implements CaseDatastoreInterface {

	//template: particular case
	private static final String caseForIdQuery = "select * from CaseDetail where caseId = ?";
	//template: all cases
	private static final String allCasesQuery = "select * from CaseDetail";
	//template: all open or closed cases
	private static final String openCasesQuery = "select * from CaseDetail where isOpen = ? order by date desc";
	//template: all notes for a specific case
	private static final String caseNotesForCaseQuery = "select * from CaseNote where caseId = ? order by caseNoteId desc";
	//template: all recent cases
	private static final String recentCasesQuery = "select * from CaseDetail order by date desc";
	//template: oldest unresolved cases
	private static final String oldestUnresolvedCasesQuery = "select * from CaseDetail where isOpen = true order by date asc";
	//template: cases for a specific category
	private static final String casesForCategoryQuery = "select CaseDetail.* from CaseDetail caseDetail, CategoryForCase categoryForCase where categoryName = ? and caseDetail.caseId = categoryForCase.caseId";
	//template: cases for an approximate date
	private static final String casesForDateLikeQuery = "select * from CaseDetail where date like ?";
	//template: cases for a date in rage
	private static final String casesForDatesQuery = "select * from CaseDetail where date >= ? and date <= ?";
	//template: suspected persons for a specific case
	private static final String suspectsForCaseQuery = "select person.* " +
								  "from Person person, Suspected suspected " +
				                  "where suspected.caseId = ? and suspected.personId = person.personId";
	//template: convicted persons for a specific case
	private static final String convictsForCaseQuery = "select *" +
								  "from Person person, Conviction conviction " +
								  "where conviction.caseId = ? and conviction.personId = person.personId";
	//template: category summary
	private static final String categorySummaryQuery = "select categoryName, count(*) as numberOfCases from CategoryForCase group by categoryName order by numberOfCases desc";
	//template: get the max id for the case note of a particular case
	private static final String maxCaseNoteIdForCaseQuery = "select max(caseNoteId) from CaseNote where caseId=?";
	//template add a new note
	private static final String insertIntoCaseNoteQuery = "insert into CaseNote " +
			"values (?, " +//caseId
			"?, " +//caseNoteId
			"?, " +//text
			"?, " +//date
			"?)";//authorUsername
	//template set case is open
	private static final String updateCaseIsOpenQuery = "update CaseDetail set isOpen = ? where caseId = ?";
	//template add Suspec
	private static final String addSuspectQuery = "insert into Suspected values (?, ?)";
	//template add new case
	private static final String insertIntoCaseDetailQuery = "insert into CaseDetail " +
							"values(?, ?, ?, ?, ? ,? ,? ,? ,?)";
	//template get the max id for the case detail
	private static final String maxCaseDetailIdQuery = "select max(caseId) from CaseDetail";
	//template remove a suspect from a case
	private static final String deleteSuspectFromCaseQuery = "delete from Suspected where caseId=? and personId=?";
	
	PreparedStatement caseForIdStatement;
	PreparedStatement allCasesStatement;
	PreparedStatement openCasesStatement;
	PreparedStatement caseNotesForCaseStatement;
	PreparedStatement oldestUnresolvedCasesStatement;
	PreparedStatement recentCasesStatement;
	PreparedStatement casesForCategoryStatement;
	PreparedStatement suspectsForCaseStatement;
	PreparedStatement convictsForCaseStatement;
	PreparedStatement categorySummaryStatement;
	PreparedStatement casesForDateLikeStatement;
	PreparedStatement casesForDatesStatement;
	PreparedStatement maxCaseNoteIdForCaseStatement;
	PreparedStatement insertIntoCaseNoteStatement;
	PreparedStatement updateCaseIsOpenStatement;
	PreparedStatement addSuspectStatement;
	PreparedStatement insertIntoCaseDetailStatement;
	PreparedStatement maxCaseDetailIdStatement;
	PreparedStatement deleteSuspectFromCaseStatement;
	
	@Override
	protected void prepareStatements() throws SQLException
	{
		caseForIdStatement = sqlConnection.prepareStatement(caseForIdQuery);
		allCasesStatement = sqlConnection.prepareStatement(allCasesQuery);
		openCasesStatement = sqlConnection.prepareStatement(openCasesQuery);
		caseNotesForCaseStatement = sqlConnection.prepareStatement(caseNotesForCaseQuery);
		recentCasesStatement = sqlConnection.prepareStatement(recentCasesQuery);
		oldestUnresolvedCasesStatement = sqlConnection.prepareStatement(oldestUnresolvedCasesQuery);
		casesForCategoryStatement = sqlConnection.prepareStatement(casesForCategoryQuery);
		suspectsForCaseStatement = sqlConnection.prepareStatement(suspectsForCaseQuery);
		convictsForCaseStatement = sqlConnection.prepareStatement(convictsForCaseQuery);
		categorySummaryStatement = sqlConnection.prepareStatement(categorySummaryQuery);
		casesForDateLikeStatement = sqlConnection.prepareStatement(casesForDateLikeQuery);
		casesForDatesStatement = sqlConnection.prepareStatement(casesForDatesQuery);
		
		maxCaseNoteIdForCaseStatement = sqlConnection.prepareStatement(maxCaseNoteIdForCaseQuery);
		maxCaseDetailIdStatement = sqlConnection.prepareStatement(maxCaseDetailIdQuery);
		
		insertIntoCaseNoteStatement = sqlConnection.prepareStatement(insertIntoCaseNoteQuery);
		updateCaseIsOpenStatement = sqlConnection.prepareStatement(updateCaseIsOpenQuery);
		addSuspectStatement =  sqlConnection.prepareStatement(addSuspectQuery);
		insertIntoCaseDetailStatement = sqlConnection.prepareStatement(insertIntoCaseDetailQuery);
		deleteSuspectFromCaseStatement = sqlConnection.prepareStatement(deleteSuspectFromCaseQuery);
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
			ResultSet rs = caseForIdStatement.executeQuery();
			if (rs.next()) {
				return new CaseDetail(rs);
			}
			return null;
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
	public List<CaseDetail> getCasesForDate(java.util.Date date) {
		return getCasesForDateLike(dateFormatter.format(date));
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
	
	@Override
	public List<CaseDetail> getCasesForDates(java.util.Date startDate, java.util.Date endDate) {
		try {
			casesForDatesStatement.setDate(1, new java.sql.Date(startDate.getTime()));
			casesForDatesStatement.setDate(2, new java.sql.Date(endDate.getTime()));
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return getResults(CaseDetail.class, casesForDatesStatement);
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
	public List<ConvictionJoinPerson> getConvictsForCase(int caseId) {
		setCaseId(convictsForCaseStatement, caseId);
		return getResults(ConvictionJoinPerson.class, convictsForCaseStatement);
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
			maxCaseNoteIdForCaseStatement.setInt(1, caseId);
			ResultSet rs = maxCaseNoteIdForCaseStatement.executeQuery();
			if (!rs.first()){ return 0; }
			return (rs.getInt(1)+1);
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	private int getNextCaseDetailId()
	{
		try {
			ResultSet rs = maxCaseDetailIdStatement.executeQuery();
			if (!rs.first()){ return 0; }
			return (rs.getInt(1)+1);
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
			try {
				insertIntoCaseNoteStatement.setInt(1, caseId);
				insertIntoCaseNoteStatement.setInt(2, caseNoteId);
				insertIntoCaseNoteStatement.setString(3, text);
				insertIntoCaseNoteStatement.setDate(4, date);
				insertIntoCaseNoteStatement.setString(5, authorUsername);
				insertIntoCaseNoteStatement.execute();
				return new CaseNote(caseId, caseNoteId, text, date, authorUsername);
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
	public CaseDetail insertIntoCaseDetail(String title, String city, String zipCode, String street, java.util.Date date, String description, String authorUsername) {
		synchronized (this.getClass()){
			try {
				int caseId = getNextCaseDetailId();
				if (caseId == -1) return null;
				insertIntoCaseDetailStatement.setInt(1, caseId);
				insertIntoCaseDetailStatement.setString(2, title);
				insertIntoCaseDetailStatement.setString(3, street);
				insertIntoCaseDetailStatement.setString(4, city);
				insertIntoCaseDetailStatement.setString(5, zipCode);
				insertIntoCaseDetailStatement.setBoolean(6, true);
				insertIntoCaseDetailStatement.setTimestamp(7, date);
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
	public boolean deleteSuspectFromCase(int caseId, int personId) {
		try {
			deleteSuspectFromCaseStatement.setInt(1, caseId);
			deleteSuspectFromCaseStatement.setInt(2, personId);
			deleteSuspectFromCaseStatement.execute();
			return deleteSuspectFromCaseStatement.getUpdateCount() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

}
