package ch.ethz.inf.dbproject.model;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Object that represents a registered in user.
 */
public final class User {

	//private final int userid;
	// TODO Decide wether we create an userid or not. (Discussion Lukas and Vincent)
	private final String username;
	private final String firstName;
	private final String lastName;
	private final String password;
	
	// TODO Decide if we really need this constructor
	public User(final String username, final String firstName, final String lastName, final String password) {
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.password = password;
	}
	
	public User(final ResultSet rs) throws SQLException {
			// Constructor
		this.username = rs.getString("username");
		this.firstName = rs.getString("firstName");
		this.lastName = rs.getString("lastName");
		this.password = rs.getString("password");
	}
	
		// Getters
	public String getUsername() {
		return username;
	}

	public String getFirstName() {
		return firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public String getUserid(){
		return username;
	}
}
