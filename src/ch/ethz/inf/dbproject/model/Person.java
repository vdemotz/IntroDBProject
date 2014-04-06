package ch.ethz.inf.dbproject.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Person extends ModelObject{

	final int personId;
	final String firstName;
	final String lastName;
	final java.util.Date birthdate;
	
	////
	//CONSTRUCTORS
	////
	
	public Person(int personId, String firstName, String lastName, Date birthdate) {
		this.personId = personId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.birthdate = birthdate;
	}
	
	public Person(int personId, String firstName, String lastName, String birthdate) throws ParseException {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		this.personId = personId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.birthdate = sdf.parse(birthdate);
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
	
	public int getId() {
		return personId;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public java.util.Date getBirthdate() {
		return birthdate;
	}
	
	public String getName() {
		return getFirstName() + " " + getLastName();
	}
	
}
