package ch.ethz.inf.dbproject.model;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Object that represents a registered in user.
 */
public final class User {

	//private final int userid; //given from template
	private final String username;
	private final String firstName;
	private final String lastName;
	
	/* given from template
	public User(final int userid, final String username, final String name) {
		this.userid = userid;
		this.username = username;
		this.name = name;
	}*/
	
	public User(final ResultSet rs) throws SQLException {

		this.username = rs.getString("username");
		this.firstName = rs.getString("firstName");
		this.lastName = rs.getString("lastName");

	}

	/*given in the template , related to the userid.
	 * public int getUserid() {
		return userid;
	}*/
	
	public String getUsername() {
		return username;
	}

	public String getFirstName() {
		return firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
}
