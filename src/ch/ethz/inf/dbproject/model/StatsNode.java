package ch.ethz.inf.dbproject.model;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Object that represents a registered in user.
 */
public final class StatsNode extends ModelObject{

	private static final long serialVersionUID = 1L;
	private String name;
	private int value;
	
	////
	//CONSTRUCTORS
	////
	
	public StatsNode(final String name, final int value) {
		this.name = name;
		this.value = value;
	}
	
	public StatsNode(ResultSet rs) throws SQLException {
		this.name = rs.getString(1);
		this.value = rs.getInt(2);
	}

	////
	// GETTERS
	////
	
	public String getName() {
		return name;
	}
	
	public String getMonth() {
		return name.substring(0, 7);
	}

	public int getValue() {
		return value;
	}
}
