package ch.ethz.inf.dbproject.model;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Object that represents a registered in user.
 */
public final class User extends ModelObject{

	private final String username;
	private final String firstName;
	private final String lastName;
	private final String password;
	
	////
	//CONSTRUCTORS
	////
	
	public User(final String username, final String firstName, final String lastName, final String password) {
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.password = password;
	}
	
	public User(final ResultSet rs) throws SQLException {
		this.username = rs.getString("username");
		this.firstName = rs.getString("firstName");
		this.lastName = rs.getString("lastName");
		this.password = rs.getString("password");
	}
	
	////
	// GETTERS
	////
	
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
	
	public String getPassword(){
		return password;
	}
}
