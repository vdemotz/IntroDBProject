package ch.ethz.inf.dbproject.model;

import ch.ethz.inf.dbproject.sqlRevisited.ResultSet;
import ch.ethz.inf.dbproject.sqlRevisited.SQLException;
import java.text.DateFormat;

/**
 * Object that represents a conviction.
 */
public final class Conviction extends ModelObject {

	private static final long serialVersionUID = 1L;
	private int convictionId;
	private java.util.Date startDate;
	private java.util.Date endDate;
	private int personId;
	private int caseId;
	
	////
	// CONSTRUCTORS
	////
	
	public Conviction(ResultSet rs) throws SQLException
	{
		super(rs);
	}

	////
	// GETTERS
	////
		
	public int getConvictionId() {
		return convictionId;
	}
	
	public java.util.Date getStartDate() {
		return startDate;
	}
	
	//returns a string in an format customary to display dates, or null if the timestamp used to create the instance was null
	public String getStartDateFormated() {
		if (startDate == null) return null;
		return DateFormat.getDateInstance().format(startDate);
	}

	//returns a string in an format customary to display dates, or null if the timestamp used to create the instance was null
	public String getEndDateFormated() {
		if (endDate == null) return null;
		return DateFormat.getDateInstance().format(endDate);
	}
	
	public java.util.Date getEndDate() {
		return endDate;
	}
	
	public int getPersonId() {
		return personId;
	}
	
	public int getCaseId() {
		return caseId;
	}
	
}
