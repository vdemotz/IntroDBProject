package ch.ethz.inf.dbproject.model;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.sql.SQLException;

public final class CaseNote extends ModelObject{
	
	private final int caseId;
	private final int caseNoteId;
	private final String text;
	private final Date date;
	private final String authorUsername;
	
	public CaseNote(final int caseId, final int caseNoteId, final String text, final Date date, final String authorUsername){
		this.caseId = caseId;
		this.text = text;
		this.date = date;
		this.authorUsername = authorUsername;
		this.caseNoteId = caseNoteId;
	}

	public CaseNote(final ResultSet rs) throws SQLException {
		this.caseId = rs.getInt("caseId");
		this.caseNoteId = rs.getInt("caseNoteId");
		this.text = rs.getString("text");
		this.date = rs.getDate("date");
		this.authorUsername = rs.getString("authorUsername");
	}
	
	public static List<? extends ModelObject> getAllModelObjectsFromResultSet(final ResultSet rs) throws SQLException
	{
		return getModelObjectsFromResultSet(rs, Integer.MAX_VALUE);
	}
	
	public static List<ModelObject> getModelObjectsFromResultSet(final ResultSet rs, int maximumCount) throws SQLException
	{
		final List<ModelObject> result = new ArrayList<ModelObject>();
		int count = 0;
		while (rs.next() && count < maximumCount) {
			result.add(new CaseNote(rs));
			count = count + 1;
		}
		return result;
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

	public Date getDate() {
		return date;
	}

	public String getAuthorUsername() {
		return authorUsername;
	}
	
}