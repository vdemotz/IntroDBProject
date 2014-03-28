package ch.ethz.inf.dbproject.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ModelObject {

	public static List<? extends ModelObject> getAllModelObjectsFromResultSet(final ResultSet rs) throws SQLException
	{
		return getModelObjectsFromResultSet(rs, Integer.MAX_VALUE);
	}

	public static List<? extends ModelObject> getModelObjectsFromResultSet(final ResultSet rs, int maximumCount) throws SQLException
	{
		return null;
	}

}
