package ch.ethz.inf.dbproject.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import ch.ethz.inf.dbproject.model.Conviction;

public class ConvictionDatastore extends Datastore implements ConvictionDatastoreInterface {

	private static final String insertIntoConvictionQuery = "insert into Conviction values (?, ?, ?, ?, ?)";
	private static final String nextConvictionIdQuery = "select coalesce (max(convictionId)+1, 1) from Conviction";
	private static final String getConvictionForIdQuery = "select * from Conviction where convictionId=?";
	
	private PreparedStatement insertIntoConvictionStatement;
	private PreparedStatement nextConvictionIdStatement;
	private PreparedStatement getConvictionForIdStatement;
	
	@Override
	protected void prepareStatements() throws SQLException
	{
		insertIntoConvictionStatement = sqlConnection.prepareStatement(insertIntoConvictionQuery);
		nextConvictionIdStatement = sqlConnection.prepareStatement(nextConvictionIdQuery);
		getConvictionForIdStatement = sqlConnection.prepareStatement(getConvictionForIdQuery);
	}
	
	////
	//QUERY
	////
	
	public Conviction getConvictionForId(int convictionId)
	{
		try {
			getConvictionForIdStatement.setInt(1, convictionId);
			getConvictionForIdStatement.execute();
			if (getConvictionForIdStatement.getResultSet().next()) {
				return new Conviction (getConvictionForIdStatement.getResultSet());
			}
		} catch (SQLException e) {
		}
		return null;
	}
	
	@Override
	public Conviction insertIntoConviction(int personId, Integer caseId, Date startDate, Date endDate) {
		synchronized(this.getClass()) {//prevent race on next conviction id
			try {
				//get and set next id
				int id = getNextConvictionId();
				insertIntoConvictionStatement.setInt(1, id);
				//set parameters
				insertIntoConvictionStatement.setInt(2, personId);
				if (caseId != null) {
					insertIntoConvictionStatement.setInt(3, caseId);
				} else {
					insertIntoConvictionStatement.setNull(3, java.sql.Types.INTEGER);
				}
				insertIntoConvictionStatement.setDate(4, new java.sql.Date(startDate.getTime()));
				insertIntoConvictionStatement.setDate(5, new java.sql.Date(endDate.getTime()));
				//execute
				insertIntoConvictionStatement.execute();
				//if all went well, return result
				if (insertIntoConvictionStatement.getUpdateCount() > 0) {
					return getConvictionForId(id);
				}
			} catch (SQLException e) {
			}
			return null;
		}
	}
	
	////
	//HELPERS
	////
	
	private int getNextConvictionId() throws SQLException
	{
		nextConvictionIdStatement.execute();
		nextConvictionIdStatement.getResultSet().next();
		return nextConvictionIdStatement.getResultSet().getInt(1);
	}

}
