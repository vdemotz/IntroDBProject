package ch.ethz.inf.dbproject.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import ch.ethz.inf.dbproject.model.StatsNode;


public class StatisticsDatastore implements StatisticsDatastoreInterface {
	
	////
	//Connection
	////
	private Connection sqlConnection;
	
	////
	//Queries
	////
	
	//select cases by city
	private final String casesPerCityString = "select city, count(*) from CaseDetail group by city";
	//select cases by month
	private final String casesPerMonthString = "select date, count(*) from CaseDetail GROUP BY MONTH(date)";
	//select convictions by month
	private final String convictionsPerMonthString = "select startDate, count(*) from Conviction group by month(startDate)";
	//select convictions by city
	private final String convictionsPerCityString = "select city, count(*) from CaseDetail as cd join Conviction as c on cd.caseId = c.caseId group by city";
	//select convictions by category
	private final String convictionsPerCategoryString = "select categoryName, count(*) from ConvictionType group by categoryName";
	//select number of notes by user
	private final String numberNotesPerUserString = "select authorCase as authorUsername, countCase+countPerson as totalNotes from "+
												"(select * from (select authorUsername as authorCase, count(*) as countCase from CaseNote "+
												"group by authorUsername) as cn join (select authorUsername as authorPerson, count(*) as countPerson "+
												"from PersonNote group by authorUsername) as pn on pn.authorPerson = cn.authorCase) as notes order by totalNotes desc";
	//select three most active categories for an user
	private final String mostActiveCategoriesForUserString = "select categoryName, count(*) as numb from CaseNote cn join CategoryForCase cfc on cn.caseId = cfc.caseId "+
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
	
	
	
	public StatisticsDatastore() {
		this.sqlConnection = MySQLConnection.getInstance().getConnection();
		try {
			prepareStatements();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void prepareStatements() throws SQLException
	{
		this.casesPerCityStatement = sqlConnection.prepareStatement(this.casesPerCityString);
		this.casesPerMonthStatement = sqlConnection.prepareStatement(this.casesPerMonthString);
		this.convictionsPerMonthStatement = sqlConnection.prepareStatement(this.convictionsPerMonthString);
		this.convictionsPerCityStatement = sqlConnection.prepareStatement(this.convictionsPerCityString);
		this.convictionsPerCategoryStatement = sqlConnection.prepareStatement(this.convictionsPerCategoryString);
		this.numberNotesPerUserStatement = sqlConnection.prepareStatement(this.numberNotesPerUserString);
		this.mostActiveCategoriesForUserStatement = sqlConnection.prepareStatement(this.mostActiveCategoriesForUserString);
	}
	
	private List<StatsNode> getListFromResultSet(PreparedStatement stat){
		try{
			ResultSet rs = stat.executeQuery();
			List<StatsNode> ret = new ArrayList<StatsNode>();
			while (rs.next()){
				ret.add(new StatsNode(rs));
			}
			return ret;
		} catch (Exception ex){
			ex.printStackTrace();
			return null;
		}
	}
	
	@Override
	public List<StatsNode> getCasesPerCity(){
		return this.getListFromResultSet(casesPerCityStatement);
	}
	
	@Override
	public List<StatsNode> getCasesPerMonth(){
		return this.getListFromResultSet(casesPerMonthStatement);
	}
	
	@Override
	public List<StatsNode> getConvictionsPerMonth(){
		return this.getListFromResultSet(convictionsPerMonthStatement);
	}
	
	@Override
	public List<StatsNode> getConvictionsPerCity(){
		return this.getListFromResultSet(convictionsPerCityStatement);
	}
	
	@Override
	public List<StatsNode> getConvictionsPerCategory(){
		return this.getListFromResultSet(convictionsPerCategoryStatement);
	}
	
	@Override
	public List<StatsNode> getNumberNotesPerUser(){
		return this.getListFromResultSet(numberNotesPerUserStatement);
	}
	
	@Override
	public List<StatsNode> getMostActiveCategoriesForUser(String username){
		try {
			this.mostActiveCategoriesForUserStatement.setString(1, username);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return this.getListFromResultSet(mostActiveCategoriesForUserStatement);
	}
}
