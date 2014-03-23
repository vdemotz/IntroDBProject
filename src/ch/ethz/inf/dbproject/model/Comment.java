package ch.ethz.inf.dbproject.model;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Object that represents a user comment.
 */
public class Comment {
	// TODO Transform it into CaseNote and PersonNote

	private final String username;
	private final String comment;
	
	public Comment(final String username, final String comment) {
		this.username = username;
		this.comment = comment;
	}

	public Comment(final ResultSet rs) throws SQLException {
		// TODO These need to be adapted to your schema
		// TODO Extra properties need to be added
		this.username = rs.getString("authorUsername");
		this.comment = rs.getString("text");
	}
	
	public String getUsername() {
		return username;
	}

	public String getComment() {
		return comment;
	}	
}
