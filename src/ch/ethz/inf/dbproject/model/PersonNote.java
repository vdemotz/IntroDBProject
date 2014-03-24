package ch.ethz.inf.dbproject.model;

import java.sql.ResultSet;
import ch.ethz.inf.dbproject.model.DatastoreInterface;

import java.util.Date;
import java.sql.SQLException;

public final class PersonNote {
	private final DatastoreInterface dbInterface = new DatastoreInterface();
	/**
	 * TODO The properties of the person note should be added here
	 */
	private final int personId;
	private final int personNoteId;
	private final String text;
	private final Date date;
	private final String authorUsername;
	
	/* TODO : See comments in CaseNote for the initialization of the key!
	*/
	public PersonNote(final int personId, final String text,
			final Date date, final String authorUsername){
		this.personId = personId;
		this.text = text;
		this.date = date;
		this.authorUsername = authorUsername;
		this.personNoteId = this.dbInterface.addPersonNote(personId, text, date, authorUsername);
	}

	public PersonNote(final ResultSet rs) throws SQLException {
		this.personId = rs.getInt("personId");
		this.personNoteId = rs.getInt("personNoteId");
		this.text = rs.getString("text");
		this.date = rs.getDate("date");
		this.authorUsername = rs.getString("authorUsername");
	}

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