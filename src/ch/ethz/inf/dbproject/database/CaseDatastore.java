package ch.ethz.inf.dbproject.database;

import java.sql.Connection;
import java.util.Date;
import java.util.List;

import ch.ethz.inf.dbproject.model.CaseDetail;
import ch.ethz.inf.dbproject.model.CaseNote;

public class CaseDatastore implements CaseDatastoreInterface {

	private Connection sqlConnection;

	public CaseDatastore() {
		this.sqlConnection = MySQLConnection.getInstance().getConnection();
	}
	
	@Override
	public CaseDetail getCaseForId(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CaseDetail> getAllCases() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CaseNote> getCaseNotesForCase(int caseID) {
		// TODO Auto-generated method stub
		return null;
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

	@Override
	public CaseNote addCaseNote(int caseId, String text, String authorUsername) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CaseDetail> getOpenCases() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CaseDetail> getOldestUnresolvedCases() {
		// TODO Auto-generated method stub
		return null;
	}

}
