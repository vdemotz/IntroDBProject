package ch.ethz.inf.dbproject.model;

import ch.ethz.inf.dbproject.sqlRevisited.ResultSet;
import ch.ethz.inf.dbproject.sqlRevisited.SQLException;

public class CategorySummary extends ModelObject {

	private static final long serialVersionUID = 1L;
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
