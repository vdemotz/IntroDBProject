package ch.ethz.inf.dbproject.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;

public final class CaseNote extends ModelObject{
	
	private static final long serialVersionUID = 1L;
	private int caseId;
	private int caseNoteId;
	private String text;
	private java.sql.Timestamp date;
	private String authorUsername;
	
	public CaseNote(final int caseId, final int caseNoteId, final String text, final java.sql.Timestamp date, final String authorUsername){
		this.caseId = caseId;
		this.text = text;
		this.date = date;
		this.authorUsername = authorUsername;
		this.caseNoteId = caseNoteId;
	}

	public CaseNote(final ResultSet rs) throws SQLException {
		super(rs);
	}
	
	////
	// GETTERS
	////

	public int getCaseId() {
		return caseId;
	}

	public int getCaseNoteId() {
		return caseNoteId;
	}

	public String getText() {
		return text;
	}

	public java.util.Date getDate() {
		return date;
	}
	
	//returns a string in an format customary to display dates and time
	public String getDateTimeFormated() {
		if (date == null) return null;
		return DateFormat.getDateTimeInstance().format(date);
	}

	public String getAuthorUsername() {
		return authorUsername;
	}
	
}