package ch.ethz.inf.dbproject.model;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.SQLException;
import java.text.DateFormat;

public final class PersonNote extends ModelObject {
	
	private int personId;
	private int personNoteId;
	private String text;
	private Timestamp date;
	private String authorUsername;
	
	////
	// CONSTRUCTORS
	////
	
	public PersonNote(final int personId, final int personNoteId, final String text, final Timestamp date, final String authorUsername){
		this.personId = personId;
		this.text = text;
		this.date = date;
		this.authorUsername = authorUsername;
		this.personNoteId = personNoteId;
	}

	public PersonNote(final ResultSet rs) throws SQLException {
		super(rs);
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

	public Timestamp getDate() {
		return date;
	}

	public String getAuthorUsername() {
		return authorUsername;
	}
	
	//returns a string in an format customary to display dates and time, or null if the timestamp used to create the instance was null
	public String getDateTimeFormated() {
		if (date == null) return null;
		return DateFormat.getDateTimeInstance().format(date);
	}
}