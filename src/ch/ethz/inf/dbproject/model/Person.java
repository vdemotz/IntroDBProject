package ch.ethz.inf.dbproject.model;

import ch.ethz.inf.dbproject.sqlRevisited.ResultSet;
import ch.ethz.inf.dbproject.sqlRevisited.SQLException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Person extends ModelObject{

	private static final long serialVersionUID = 1L;
	private int personId;
	private String firstName;
	private String lastName;
	private java.util.Date birthdate;
	
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
		super(rs);
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
		String retFirstName = getFirstName();
		String retLastName = getLastName();
		if (retFirstName == null){
			retFirstName = "(?)";
		}
		if (retLastName == null){
			retLastName = "(?)";
		}
		return retFirstName + " " + retLastName;
	}
	
}
