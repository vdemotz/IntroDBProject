package ch.ethz.inf.dbproject.model;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Object that represents a registered in user.
 */
public final class StatsNode extends ModelObject{

	private final String name;
	private final int value;
	
	////
	//CONSTRUCTORS
	////
	
	public StatsNode(final String name, final int value) {
		this.name = name;
		this.value = value;
	}
	
	public StatsNode(final ResultSet rs) throws SQLException {
		Object firstCol =  rs.getObject(1);
		if (firstCol instanceof java.sql.Date || firstCol instanceof java.sql.Timestamp){
			this.name = (firstCol.toString()).substring(0, 7);
		} else {
			this.name = firstCol.toString();
		}
		this.value = rs.getInt(2);
	}

	////
	// GETTERS
	////
	
	public String getName() {
		return name;
	}

	public int getValue() {
		return value;
	}
}
