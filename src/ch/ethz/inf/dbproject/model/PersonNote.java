package ch.ethz.inf.dbproject.model;

import ch.ethz.inf.dbproject.sqlRevisited.ResultSet;
import ch.ethz.inf.dbproject.sqlRevisited.SQLException;
import java.util.Date;
import java.text.DateFormat;

public final class PersonNote extends ModelObject {
	
	private static final long serialVersionUID = 1L;
	private int personId;
	private int personNoteId;
	private String text;
	private Date date;
	private String authorUsername;
	
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

	public Date getDate() {
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