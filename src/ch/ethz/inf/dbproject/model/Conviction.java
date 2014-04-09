package ch.ethz.inf.dbproject.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;

/**
 * Object that represents a conviction.
 */
public final class Conviction extends ModelObject {

	private int convictionId;
	private java.sql.Date startDate;
	private java.sql.Date endDate;

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
	
	public java.sql.Date getStartDate() {
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

	
	public java.sql.Date getEndDate() {
		return endDate;
	}
	
}
