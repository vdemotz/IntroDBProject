package ch.ethz.inf.dbproject.database;

import ch.ethz.inf.dbproject.sqlRevisited.SQLException;
import java.util.Date;
import ch.ethz.inf.dbproject.sqlRevisited.PreparedStatement;
import ch.ethz.inf.dbproject.sqlRevisited.ResultSet;
import ch.ethz.inf.dbproject.sqlRevisited.SQLType;

import ch.ethz.inf.dbproject.model.Conviction;

public class ConvictionDatastore extends Datastore implements ConvictionDatastoreInterface {

	private static final String insertIntoConvictionQuery = "insert into Conviction values (?, ?, ?, ?, ?)";
	private static final String maxConvictionIdQuery = "select max(convictionId) from Conviction";
	private static final String getConvictionForIdQuery = "select * from Conviction where convictionId=?";
	
	private PreparedStatement insertIntoConvictionStatement;
	private PreparedStatement maxConvictionIdStatement;
	private PreparedStatement getConvictionForIdStatement;
	
	@Override
	protected void prepareStatements() throws SQLException
	{
		insertIntoConvictionStatement = sqlConnection.prepareStatement(insertIntoConvictionQuery);
		maxConvictionIdStatement = sqlConnection.prepareStatement(maxConvictionIdQuery);
		getConvictionForIdStatement = sqlConnection.prepareStatement(getConvictionForIdQuery);
	}
	
	////
	//QUERY
	////
	
	public Conviction getConvictionForId(int convictionId)
	{
		try {
			getConvictionForIdStatement.setInt(1, convictionId);
			ResultSet rs = getConvictionForIdStatement.executeQuery();
			if (rs.next()) {
				return new Conviction (rs);
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
					insertIntoConvictionStatement.setNull(3, SQLType.BaseType.Integer);
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
	
	private int getNextConvictionId() {
		try{
			ResultSet rs = maxConvictionIdStatement.executeQuery();
			if (!rs.first()){ return 0; }
			return (rs.getInt("max(convictionId)")+1);
		} catch (final SQLException ex){
			ex.printStackTrace();
			return -1;
		}
	}

}
