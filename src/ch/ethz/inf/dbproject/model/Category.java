package ch.ethz.inf.dbproject.model;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Object that represents a type of crime (i.e. Theft, Assault...) 
 */
public final class Category extends ModelObject {

	private final String name;

	////
	//CONSTRUCTOR
	////
	
	public Category(final String name) {
		this.name = name;
	}
	
	public Category(ResultSet rs) throws SQLException
	{
		this(rs.getString("name"));
	}

	////
	// GETTERS
	////
	
	public final String getName() {
		return name;
	}
	
}
