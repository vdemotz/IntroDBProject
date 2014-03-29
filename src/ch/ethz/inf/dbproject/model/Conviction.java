package ch.ethz.inf.dbproject.model;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Object that represents a conviction.
 */
public class Conviction extends ModelObject {

	private final java.sql.Date date;
	private final java.sql.Date endDate;
	private final String type;

	////
	// CONSTRUCTORS
	////
	
	public Conviction(final java.sql.Date date, final java.sql.Date endDate, final String type) {
		this.date = date;
		this.endDate = endDate;
		this.type = type;
	}
	
	public Conviction(ResultSet rs) throws SQLException
	{
		this(rs.getDate("startDate"), rs.getDate("endDate"), rs.getString("type"));
	}
	
	////
	// GETTERS
	////
	
	public java.sql.Date getDate() {
		return date;
	}

	public java.sql.Date getEndDate() {
		return endDate;
	}

	public String getType() {
		return type;
	}
	
		
}
