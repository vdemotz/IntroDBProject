package ch.ethz.inf.dbproject.model;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.sql.SQLException;

public final class CaseDetail {

	private final int caseId;
	private final String title;
	private final String city;
	private final String street;
	private final String zipCode;
	private final boolean isOpen;
	private final Date date;
	private final String description;
	private final String authorName;

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
		this.caseId = rs.getInt("caseId");
		this.title = rs.getString("title");
		this.city = rs.getString("city");
		this.street = rs.getString("street");
		this.zipCode = rs.getString("zipCode");
		this.isOpen = rs.getBoolean("isOpen");
		this.date = rs.getDate("date");
		this.description = rs.getString("description");
		this.authorName = rs.getString("authorName");
	}
	
	public static List<CaseDetail> getAllCaseDetailsFromResultSet(final ResultSet rs) throws SQLException
	{
		return getCaseDetailsFromResultSet(rs, Integer.MAX_VALUE);
	}
	
	public static List<CaseDetail> getCaseDetailsFromResultSet(final ResultSet rs, int maximumCount) throws SQLException
	{
		final List<CaseDetail> result = new ArrayList<CaseDetail>();
		int count = 0;
		while (rs.next() && count < maximumCount) {
			result.add(new CaseDetail(rs));
			count = count + 1;
		}
		return result;
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
	
	public Date getDate() {
		return date;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getAuthorName() {
		return authorName;
	}
	
	public String getLocation() {
		return (this.getZipCode()+" "+this.getCity()+", "+this.getStreet());
	}
	
}