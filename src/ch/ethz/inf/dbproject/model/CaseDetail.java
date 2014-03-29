package ch.ethz.inf.dbproject.model;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;
import java.sql.SQLException;

public final class CaseDetail extends ModelObject {

	private int caseId;
	private String title;
	private String city;
	private String street;
	private String zipCode;
	private boolean isOpen;
	private java.sql.Date date;
	private String description;
	private String authorName;

	////
	//CONSTRUCTORS
	////
	
	public CaseDetail(final int id, final String tit, final String city, final String street, final String zipCode, final boolean isOp, final Date da, final String desc, final String authorN) {
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
	
	public java.sql.Date getDate() {
		return date;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getAuthorName() {
		return authorName;
	}
	
	public String getLocation() {
		return (this.getZipCode()+" "+this.getCity()+", "+this.getStreet());//TODO: nicely handle null
	}
	
}