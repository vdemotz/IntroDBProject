package ch.ethz.inf.dbproject.model;

import java.sql.ResultSet;
import java.text.DateFormat;

public class ConvictionJoinPerson extends ModelObject {

	private int convictionId;
	private java.sql.Date startDate;
	private java.sql.Date endDate;
	private int caseId;
	private int personId;
	private String firstName;
	private String lastName;
	private java.util.Date birthdate;

	public ConvictionJoinPerson(ResultSet rs)
	{
		super(rs);
	}
	
	//returns a string in an format customary to display dates, or null if the timestamp used to create the instance was null
	public String getStartDateFormatted() {
		if (startDate == null) return null;
		return DateFormat.getDateInstance().format(startDate);
	}

	//returns a string in an format customary to display dates, or null if the timestamp used to create the instance was null
	public String getEndDateFormatted() {
		if (endDate == null) return null;
		return DateFormat.getDateInstance().format(endDate);
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

	public int getConvictionId() {
		return convictionId;
	}

	public java.sql.Date getStartDate() {
		return startDate;
	}

	public java.sql.Date getEndDate() {
		return endDate;
	}

	public int getCaseId() {
		return caseId;
	}

	public int getPersonId() {
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
	
}
