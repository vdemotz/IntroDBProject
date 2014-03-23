package ch.ethz.inf.dbproject.model;

import java.sql.ResultSet;
import java.util.Date;
import java.sql.SQLException;

public final class Case {

	/**
	 * TODO The properties of the case should be added here
	 */
	private final int caseId;
	private final String title;
	private final String location;
	private final boolean isOpen;
	private final Date date;
	private final String description;
	private final String authorName;

	/**
	 * Construct a new case.
	 * 
	 * @param description
	 *            The name of the case
	 */
	public Case(final int id, final String tit, final String loc,
			final boolean isOp, final Date da, final String desc,
			final String authorN) {
		this.caseId = id;
		this.title = tit;
		this.location = loc;
		this.isOpen = isOp;
		this.date = da;
		this.description = desc;
		this.authorName = authorN;
	}

	public Case(final ResultSet rs) throws SQLException {
		// TODO These need to be adapted to your schema
		// TODO Extra properties need to be added
		this.caseId = rs.getInt("caseId");
		this.title = rs.getString("title");
		this.location = rs.getString("location");
		this.isOpen = rs.getBoolean("isOpen");
		this.date = rs.getDate("date");
		this.description = rs.getString("description");
		this.authorName = rs.getString("authorName");
	}

	/**
	 * HINT: In eclipse, use Alt + Shirt + S menu and choose: "Generate Getters
	 * and Setters to auto-magically generate the getters.
	 */
	public int getCaseId() {
		return caseId;
	}
	
	public int getId() {
		return caseId;
	}

	public String getTitle() {
		return title;
	}
	
	public String getLocation() {
		return location;
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
}