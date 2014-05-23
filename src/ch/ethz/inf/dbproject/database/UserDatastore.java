package ch.ethz.inf.dbproject.database;

import java.sql.*;
import java.util.List;
import java.sql.PreparedStatement;
import ch.ethz.inf.dbproject.model.CaseDetail;
import ch.ethz.inf.dbproject.model.User;

public class UserDatastore extends Datastore implements UserDatastoreInterface {

	////
	// String for Prepared Statement
	////
	
	//particular user for an username and a password
	private static final String getUserForUsernameAndPasswordString = "select * from User where username = ? and password = ?";
	//cases for a particular user
	/*private static final String getCurrentCasesForUserString = "(select caseDetail.* from CaseDetail caseDetail, CaseNote caseNote where caseNote.authorUsername like ? and caseNote.caseId = caseDetail.caseId) "+
	"union "+
	"(select caseDetail2.* from CaseDetail caseDetail2 where authorName like ?) order by date desc";*/
	private static final String getCurrentCasesForUserString = "select caseId, title, street, city, zipCode, isOpen, date, description, authorName from ((select caseDetail.* from CaseDetail caseDetail, CaseNote caseNote where caseNote.authorUsername = ? and caseNote.caseId = caseDetail.caseId) "+
			"union "+
			"(select CaseDetail.* from CaseDetail caseDetail where caseDetail.authorName = ?))as P order by date desc";
	//add User
	private static final String addUserString = "insert into User values(?, ?, ?, ?)";
	//check if username is available
	private static final String isUsernameAvailableString = "select * from User where username = ?";
	
	////
	//Prepared Statements
	////
	
	private PreparedStatement getUserForUsernameAndPasswordStatement;
	private PreparedStatement getCurrentCasesForUserStatement;
	private PreparedStatement addUserStatement;
	private PreparedStatement isUsernameAvailableStatement;
	
	////
	//Constructor
	////
	
	@Override
	protected void prepareStatements() throws SQLException {
		getUserForUsernameAndPasswordStatement = sqlConnection.prepareStatement(getUserForUsernameAndPasswordString);
		getCurrentCasesForUserStatement = sqlConnection.prepareStatement(getCurrentCasesForUserString);
		addUserStatement = sqlConnection.prepareStatement(addUserString);
		isUsernameAvailableStatement = sqlConnection.prepareStatement(isUsernameAvailableString);
	}
	
	////
	//Return type User
	////
	@Override
	public User getUserForUsernameAndPassword(String username, String password) {
		try{
			getUserForUsernameAndPasswordStatement.setString(1, username);
			getUserForUsernameAndPasswordStatement.setString(2, password);
			ResultSet rs = getUserForUsernameAndPasswordStatement.executeQuery();
			if (!rs.first()){return null;}
			return new User(rs);
			
		} catch (final SQLException ex){
			ex.printStackTrace();
			return null;
		}
	}

	////
	//Return type List<CaseDetail>
	////
	@Override
	public List<CaseDetail> getCurrentCasesForUser(String username) {
		try{
			getCurrentCasesForUserStatement.setString(1, username);
			getCurrentCasesForUserStatement.setString(2, username);
			return getResults(CaseDetail.class, getCurrentCasesForUserStatement);
		} catch (final SQLException ex){
			System.err.println("failed to retrieve cases from user");
			ex.printStackTrace();
			return null;
		}
	}
	
	
	////
	//Add
	////

	@Override
	public User addUser(String username, String password, String lastName, String firstName) {
		try{
			addUserStatement.setString(1, username);
			addUserStatement.setString(2, firstName);
			addUserStatement.setString(3, lastName);
			addUserStatement.setString(4, password);
			addUserStatement.execute();
			return new User(username, firstName, lastName, password);
		} catch (final SQLException ex){
			System.err.println("failed to add user");
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean isUsernameAvailable(String username) {
		try{
			isUsernameAvailableStatement.setString(1, username);
			ResultSet rs = isUsernameAvailableStatement.executeQuery();
			if (!rs.first()){ return false; }
			return true;
			 
		} catch (final SQLException ex){
			System.err.println("failed to check if user is in DB");
			ex.printStackTrace();
			return false;
		}
	}
}
