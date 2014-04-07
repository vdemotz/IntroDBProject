package ch.ethz.inf.dbproject.model;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;
import java.text.DateFormat;

public final class CaseDetail extends ModelObject {

	private int caseId;
	private String title;
	private String city;
	private String street;
	private String zipCode;
	private boolean isOpen;
	private String description;
	private String authorName;
	private java.sql.Timestamp date;
	
	////
	//CONSTRUCTORS
	////
	
	public CaseDetail(final int id, final String tit, final String city, final String street, final String zipCode, final boolean isOp, final java.sql.Timestamp da, final String desc, final String authorN) {
		this.caseId = id;
		this.title = tit;
		this.city = city;
		this.street = street;
		this.zipCode = zipCode;
		this.isOpen = isOp;
		this.date = da;
		this.description = desc;
		this.authorName = authorN;
	}
	
	public CaseDetail(final ResultSet rs) throws SQLException {
		super(rs);
	}

	////
	// GETTERS
	////
	
	public int getCaseId() {
		return caseId;
	}
	
	public int getId() {
		return caseId;
	}

	public String getTitle() {
		return title;
	}
	
	public String getCity() {
		return city;
	}

	public String getStreet() {
		return street;
	}

	public String getZipCode() {
		return zipCode;
	}
	
	public boolean getIsOpen() {
		return isOpen;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getAuthorName() {
		return authorName;
	}
	
	public java.util.Date getDate() {
		return date;
	}
	
	//returns a string in an format customary to display dates and time, or null if the timestamp used to create the instance was null
	public String getDateTimeFormated() {
		if (date == null) return null;
		return DateFormat.getDateTimeInstance().format(date);
	}
	
	//returns a string in an format customary to display dates, or null if the timestamp used to create the instance was null
	public String getDateFormated() {
		if (date == null) return null;
		return DateFormat.getDateInstance().format(date);
	}
	
	public String getLocation() {
		String result = "";
		if (getZipCode() != null) {
			result = result+getZipCode();
		}
		if (getCity() != null) {
			if (getZipCode() != null) result = result + " ";
			result = result + getCity();
		}
		
		if (getStreet() != null) {
			if (getCity() != null || getZipCode() != null) result = result + ", ";
			result = result + getStreet();
		}
		if (result == "") return null;
		return result;
	}
	
	
}