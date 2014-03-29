package ch.ethz.inf.dbproject.model;

import java.sql.ResultSet;
import java.util.Date;
import java.sql.SQLException;

public final class PersonNote extends ModelObject {
	
	private final int personId;
	private final int personNoteId;
	private final String text;
	private final Date date;
	private final String authorUsername;
	
	////
	// CONSTRUCTORS
	////
	
	public PersonNote(final int personId, final int personNoteId, final String text, final Date date, final String authorUsername){
		this.personId = personId;
		this.text = text;
		this.date = date;
		this.authorUsername = authorUsername;
		this.personNoteId = personNoteId;
	}

	public PersonNote(final ResultSet rs) throws SQLException {
		this.personId = rs.getInt("personId");
		this.personNoteId = rs.getInt("personNoteId");
		this.text = rs.getString("text");
		this.date = rs.getDate("date");
		this.authorUsername = rs.getString("authorUsername");
	}
	
	////
	// GETTERS
	////
	
	public int getPersonId() {
		return personId;
	}

	public int getPersonNoteId() {
		return personNoteId;
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