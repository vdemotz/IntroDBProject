package ch.ethz.inf.dbproject.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

public class Person extends ModelObject{

	final int personId;
	final String firstName;
	final String lastName;
	final java.sql.Date birthdate;
	
	////
	//CONSTRUCTORS
	////
	
	public Person(int personId, String firstName, String lastName, Date birthdate) {
		this.personId = personId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.birthdate = birthdate;
	}

	public Person(final ResultSet rs) throws SQLException {
		this.personId = rs.getInt("personId");
		this.firstName = rs.getString("firstName");
		this.lastName = rs.getString("lastName");
		this.birthdate = rs.getDate("birthdate");
	}
	
	////
	//GETTERS
	////
	
	public int getPersonId() {
		return personId;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public java.sql.Date getBirthdate() {
		return birthdate;
	}
	
	
}
