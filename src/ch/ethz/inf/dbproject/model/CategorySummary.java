package ch.ethz.inf.dbproject.model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CategorySummary extends ModelObject {

	private String categoryName;
	private long numberOfCases;
	
	public CategorySummary(ResultSet rs) throws SQLException {
		super(rs);
	}

	public String getCategoryName() {
		return categoryName;
	}

	public long getNumberOfCases() {
		return numberOfCases;
	}

}
