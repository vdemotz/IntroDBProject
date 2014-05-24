package ch.ethz.inf.dbproject.model;

import ch.ethz.inf.dbproject.sqlRevisited.ResultSet;
import ch.ethz.inf.dbproject.sqlRevisited.SQLException;


/**
 * Object that represents a type of crime (i.e. Theft, Assault...) 
 */
public final class Category extends ModelObject {

	private static final long serialVersionUID = 1L;
	private String name;

	////
	//CONSTRUCTOR
	////
	
	public Category(final String name) {
		this.name = name;
	}
	
	public Category(ResultSet rs) throws SQLException {
		super(rs);
	}

	////
	// GETTERS
	////
	
	public final String getName() {
		return name;
	}
	
}
