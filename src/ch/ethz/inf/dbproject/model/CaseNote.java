package ch.ethz.inf.dbproject.model;

import java.sql.ResultSet;
import ch.ethz.inf.dbproject.model.DatastoreInterface;

import java.util.Date;
import java.sql.SQLException;

public final class CaseNote {
	private final DatastoreInterface dbInterface = new DatastoreInterface();
	/**
	 * TODO The properties of the case should be added here
	 */
	private final int caseId;
	private final int caseNoteId;
	private final String text;
	private final Date date;
	private final String authorUsername;

	/**
	 * Construct a new case.
	 * 
	 * @param description
	 *            The name of the case
	 */
	public CaseNote(final int caseId, final String text,
			final Date date, final String authorUsername){
		this.caseId = caseId;
		this.text = text;
		this.date = date;
		this.authorUsername = authorUsername;
		this.caseNoteId = this.dbInterface.addCaseNote(caseId, text, date, authorUsername);
	}

	public CaseNote(final ResultSet rs) throws SQLException {
		this.caseId = rs.getInt("caseId");
		this.caseNoteId = rs.getInt("caseNoteId");
		this.text = rs.getString("text");
		this.date = rs.getDate("date");
		this.authorUsername = rs.getString("authorUsername");
	}
	
	public int getCaseId() {
		return caseId;
	}

	public int getCaseNoteId() {
		return caseNoteId;
	}

	public String getText() {
		return text;
	}

	public Date getDate() {
		return date;
	}

	public String getAuthorUsername() {
		return authorUsername;
	}
}