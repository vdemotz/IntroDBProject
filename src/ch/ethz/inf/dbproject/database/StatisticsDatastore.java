package ch.ethz.inf.dbproject.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import ch.ethz.inf.dbproject.model.StatsNode;


public class StatisticsDatastore extends Datastore implements StatisticsDatastoreInterface {
	
	////
	//Queries
	////
	
	//select cases by city
	private static final String casesPerCityString = "select city, count(*) from CaseDetail group by city";
	//select cases by month
	private static final String casesPerMonthString = "select date, count(*) from CaseDetail GROUP BY MONTH(date)";
	//select convictions by month
	private static final String convictionsPerMonthString = "select startDate, count(*) from Conviction group by month(startDate) order by startDate asc";
	//select convictions by city
	private static final String convictionsPerCityString = "select city, count(*) from CaseDetail as cd join Conviction as c on cd.caseId = c.caseId group by city";
	//select convictions by category
	private static final String convictionsPerCategoryString = "select categoryName, count(*) " +
													    "from Conviction conviction, CategoryForCase categoryForCase " +
													    "where conviction.caseId = categoryForCase.caseId " +
													    "group by categoryName";
	//select number of notes by user
	private static final String numberNotesPerUserString = "select notes.authorUsername, sum(notes.Count) as numbN from "+
														"(select authorUsername, count(*) as count from CaseNote group by authorUsername "+
														"UNION ALL "+
														"select authorUsername, count(*) as count from PersonNote group by authorUsername) as notes "+
														"group by authorUsername order by numbN desc limit 3;";
	//select three most active categories for an user
	private static final String mostActiveCategoriesForUserString = "select categoryName, count(*) as numb from CaseNote cn join CategoryForCase cfc on cn.caseId = cfc.caseId "+
															"where cn.authorUsername = ? group by categoryName order by numb desc limit 3";
	
	////
	//Prepared Statement
	////
	
	private PreparedStatement casesPerCityStatement;
	private PreparedStatement casesPerMonthStatement;
	private PreparedStatement convictionsPerMonthStatement;
	private PreparedStatement convictionsPerCityStatement;
	private PreparedStatement convictionsPerCategoryStatement;
	private PreparedStatement numberNotesPerUserStatement;
	private PreparedStatement mostActiveCategoriesForUserStatement;
	
	@Override
	protected void prepareStatements() throws SQLException
	{
		this.casesPerCityStatement = sqlConnection.prepareStatement(casesPerCityString);
		this.casesPerMonthStatement = sqlConnection.prepareStatement(casesPerMonthString);
		this.convictionsPerMonthStatement = sqlConnection.prepareStatement(convictionsPerMonthString);
		this.convictionsPerCityStatement = sqlConnection.prepareStatement(convictionsPerCityString);
		this.convictionsPerCategoryStatement = sqlConnection.prepareStatement(convictionsPerCategoryString);
		this.numberNotesPerUserStatement = sqlConnection.prepareStatement(numberNotesPerUserString);
		this.mostActiveCategoriesForUserStatement = sqlConnection.prepareStatement(mostActiveCategoriesForUserString);
	}
	
	////
	//QUERY
	////
	
	@Override
	public List<StatsNode> getCasesPerCity(){
		return getResults(StatsNode.class, casesPerCityStatement);
	}
	
	@Override
	public List<StatsNode> getCasesPerMonth(){
		return getResults(StatsNode.class, casesPerMonthStatement);
	}
	
	@Override
	public List<StatsNode> getConvictionsPerMonth(){
		return getResults(StatsNode.class, convictionsPerMonthStatement);
	}
	
	@Override
	public List<StatsNode> getConvictionsPerCity(){
		return getResults(StatsNode.class, convictionsPerCityStatement);
	}
	
	@Override
	public List<StatsNode> getConvictionsPerCategory(){
		return getResults(StatsNode.class, convictionsPerCategoryStatement);
	}
	
	@Override
	public List<StatsNode> getNumberNotesPerUser(){
		return getResults(StatsNode.class, numberNotesPerUserStatement);
	}
	
	@Override
	public List<StatsNode> getMostActiveCategoriesForUser(String username){
		try {
			this.mostActiveCategoriesForUserStatement.setString(1, username);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return getResults(StatsNode.class, mostActiveCategoriesForUserStatement);
	}
}
