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
	
	/* TODO : find a clever way to get the unique ID of a new case note, at creation.
	*
	* My idea was : when a class, somewhere else (mainly in CaseServlet of course), create
	* a new CaseNote given it attributes the constructor does :
	* - initialize all fields but caseNoteId with given attributes
	* - connect to database, create a CaseNote (via addCaseNote) in it and have it return an unique ID
	* - initialize the field caseNoteId, consistent with the DB
	* 
	* If you have a better idea, go ahead. But I just points out the benefit of that way of doing :
	* 
	* - the creation of a case note in java is always consistent with the DB
	* - we don't need (yet) to iterate through all the existing caseNoteId's
	* - we don't have to create separately the CaseNote in the DB
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