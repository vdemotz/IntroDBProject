package ch.ethz.inf.dbproject.model;

import java.util.Date;

public class Person {

	final int personId;
	final String firstName;
	final String lastName;
	final Date birthdate;
	
	////
	//CONSTRUCTORS
	////
	
	public Person(int personId, String firstName, String lastName, Date birthdate) {
		this.personId = personId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.birthdate = birthdate;
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

	public Date getBirthdate() {
		return birthdate;
	}
	
	
}
