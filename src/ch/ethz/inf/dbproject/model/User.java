package ch.ethz.inf.dbproject.model;

import ch.ethz.inf.dbproject.sqlRevisited.ResultSet;
import ch.ethz.inf.dbproject.sqlRevisited.SQLException;

/**
 * Object that represents a registered in user.
 */
public final class User extends ModelObject{

	private static final long serialVersionUID = 1L;
	private String username;
	private String firstName;
	private String lastName;
	private String password;
	
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
		super(rs);
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
