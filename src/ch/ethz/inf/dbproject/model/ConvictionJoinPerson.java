package ch.ethz.inf.dbproject.model;

import ch.ethz.inf.dbproject.sqlRevisited.ResultSet;
import java.text.DateFormat;

public class ConvictionJoinPerson extends ModelObject {

	private static final long serialVersionUID = 1L;
	private int convictionId;
	private java.util.Date startDate;
	private java.util.Date endDate;
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

	public java.util.Date getStartDate() {
		return startDate;
	}

	public java.util.Date getEndDate() {
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
