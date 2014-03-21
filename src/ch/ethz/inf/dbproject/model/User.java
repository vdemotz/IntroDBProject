package ch.ethz.inf.dbproject.model;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Object that represents a registered in user.
 */
public final class User {

	private final int userid;
	private final String username;
	private final String name;
	
	public User(final int userid, final String username, final String name) {
		this.userid = userid;
		this.username = username;
		this.name = name;
	}
	
	public User(final ResultSet rs) throws SQLException {
		// TODO These need to be adapted to your schema
		// TODO Extra properties need to be added
		this.userid = 1;
		this.username = rs.getString("name");
		this.name = rs.getString("password");

	}

	public int getUserid() {
		return userid;
	}
	
	public String getUsername() {
		return username;
	}

	public String getName() {
		return name;
	}	
}
