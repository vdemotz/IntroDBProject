package ch.ethz.inf.dbproject.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import ch.ethz.inf.dbproject.model.CaseDetail;
import ch.ethz.inf.dbproject.model.CaseNote;

public class CaseDatastore implements CaseDatastoreInterface {

	private Connection sqlConnection;

	//template: particular case
	String caseForIdQuery = "select * from CaseDetail where caseId = ?";
	//template: all cases
	String allCasesQuery = "select * from CaseDetail";
	//template: all open or closed cases
	String openCasesQuery = "select * from CaseDetail where isOpen = ? order by date desc";
	//template: all notes for a specific case
	String caseNotesForCaseQuery = "select caseNote.* from CaseNote caseNote where caseNote.caseId = ?";
	//template: all recent cases
	String recentCasesQuery = "select * from CaseDetail order by date desc";
	//template: oldest unresolved cases
	String oldestUnresolvedCasesQuery = "select * from CaseDetail where isOpen = true order by date asc";
	
	PreparedStatement caseForIdStatement;
	PreparedStatement allCasesStatement;
	PreparedStatement openCasesStatement;
	PreparedStatement caseNotesForCaseStatement;
	PreparedStatement oldestUnresolvedCasesStatement;
	PreparedStatement recentCasesStatement;
	PreparedStatement casesForCategoryStatement;
	
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
	}
	
	
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
	
	@SuppressWarnings("unchecked")
	private List<CaseDetail> executeCaseDetailListStatement(PreparedStatement statement)
	{
		 try {
			statement.execute();
			return (List<CaseDetail>)CaseDetail.getAllModelObjectsFromResultSet(statement.getResultSet());
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public List<CaseDetail> getAllCases() {
		return executeCaseDetailListStatement(allCasesStatement);
	}

	@Override
	public List<CaseDetail> getOpenCases() {
		try {
			openCasesStatement.setBoolean(1, true);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return executeCaseDetailListStatement(openCasesStatement);
	}

	@Override
	public List<CaseDetail> getOldestUnresolvedCases() {
		return executeCaseDetailListStatement(oldestUnresolvedCasesStatement);
	}

	@Override
	public List<CaseDetail> getClosedCases() {
		try {
			openCasesStatement.setBoolean(1, false);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return executeCaseDetailListStatement(openCasesStatement);
	}

	@Override
	public List<CaseDetail> getRecentCases() {
		return executeCaseDetailListStatement(recentCasesStatement);
	}
	
	@Override
	public List<CaseDetail> getCasesForCategory(String name) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<CaseDetail> getCasesForDate(Date date) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	////
	//Result of type List<CaseNote>
	////
	
	@SuppressWarnings("unchecked")
	private List<CaseNote> executeCaseNoteListStatement(PreparedStatement statement)
	{
		 try {
			statement.execute();
			return (List<CaseNote>)CaseNote.getAllModelObjectsFromResultSet(statement.getResultSet());
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public List<CaseNote> getCaseNotesForCase(int caseId) {
		try {
			caseNotesForCaseStatement.setInt(1, caseId);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return executeCaseNoteListStatement(caseNotesForCaseStatement);
	}
	


	@Override
	public CaseNote addCaseNote(int caseId, String text, String authorUsername) {
		// TODO Auto-generated method stub
		return null;
	}



}
