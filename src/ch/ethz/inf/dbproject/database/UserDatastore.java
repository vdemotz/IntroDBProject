package ch.ethz.inf.dbproject.database;

import ch.ethz.inf.dbproject.sqlRevisited.ResultSet;
import ch.ethz.inf.dbproject.sqlRevisited.SQLException;
import ch.ethz.inf.dbproject.sqlRevisited.PreparedStatement;
import java.util.List;
import ch.ethz.inf.dbproject.model.CaseDetail;
import ch.ethz.inf.dbproject.model.User;

public class UserDatastore extends Datastore implements UserDatastoreInterface {

	////
	// String for Prepared Statement
	////
	
	//particular user for an username and a password
	private static final String getUserForUsernameAndPasswordString = "select * from User where username = ? and password = ?";
	//add User
	private static final String addUserString = "insert into User values(?, ?, ?, ?)";
	//check if username is available
	private static final String isUsernameAvailableString = "select * from User where username = ?";
	
	////
	//Prepared Statements
	////
	
	private PreparedStatement getUserForUsernameAndPasswordStatement;
	private PreparedStatement addUserStatement;
	private PreparedStatement isUsernameAvailableStatement;
	
	////
	//Constructor
	////
	
	@Override
	protected void prepareStatements() throws SQLException {
		getUserForUsernameAndPasswordStatement = sqlConnection.prepareStatement(getUserForUsernameAndPasswordString);
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
