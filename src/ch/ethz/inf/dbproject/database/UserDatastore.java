package ch.ethz.inf.dbproject.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.PreparedStatement;
import ch.ethz.inf.dbproject.model.CaseDetail;
import ch.ethz.inf.dbproject.model.User;

public class UserDatastore implements UserDatastoreInterface {

		private Connection sqlConnection;
		
		////
		// String for Prepared Statement
		////
	private String getUserForUsernameAndPassword = "select * from User where username = ? and password = ?";
	private String getCurrentCasesForUser = "(select caseDetail.* from CaseDetail caseDetail, CaseNote caseNote where caseNote.authorUsername = ? and caseNote.caseId = caseDetail.caseId) "+
	"union "+
	"(select CaseDetail.* from CaseDetail caseDetail where caseDetail.authorName = ?) order by date desc";
	private String addUser = "insert into User(username, firstName, lastName, password) values(?, ?, ?, ?)";
	private String isUsernameAvailable = "select * from User where username = ?";
	
	/**
	 * new UserDatastore with connection to database
	 */
	public UserDatastore() {
			this.sqlConnection = MySQLConnection.getInstance().getConnection();
	}
	
	@Override
	public User getUserForUsernameAndPassword(String username, String password) {
		try{
			PreparedStatement sqlRequest = sqlConnection.prepareStatement(this.getUserForUsernameAndPassword);
			sqlRequest.setString(1, username);
			sqlRequest.setString(2, password);
			ResultSet rs = sqlRequest.executeQuery();
			if (!rs.first()){
				sqlRequest.close();
				rs.close();
				return null;
			}
			User usr = new User(rs);
			sqlRequest.close();
			rs.close();
			return usr;
			
		} catch (final SQLException ex){
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public List<CaseDetail> getCurrentCasesForUser(String username) {
		try{
			PreparedStatement sqlRequest = sqlConnection.prepareStatement(this.getCurrentCasesForUser);
			sqlRequest.setString(1, username);
			sqlRequest.setString(2, username);
			ResultSet rs = sqlRequest.executeQuery();
			List<CaseDetail> lcd = new ArrayList<CaseDetail>();
			
			while(rs.next()){
				lcd.add(new CaseDetail(rs));
			}
			sqlRequest.close();
			rs.close();
			return lcd;
			
		} catch (final SQLException ex){
			System.err.println("failed to retrieve cases from user");
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public User addUser(String username, String password, String lastName, String firstName) {
		try{
			PreparedStatement sqlRequest = sqlConnection.prepareStatement(this.addUser);
			sqlRequest.setString(1, username);
			sqlRequest.setString(2, firstName);
			sqlRequest.setString(3, lastName);
			sqlRequest.setString(4, password);
			sqlRequest.execute();
			sqlRequest.close();
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
			PreparedStatement sqlRequest = sqlConnection.prepareStatement(this.isUsernameAvailable);
			sqlRequest.setString(1, username);
			ResultSet rs = sqlRequest.executeQuery();
			if (!rs.first()){
				sqlRequest.close();
				rs.close();
				return false;
			}
			sqlRequest.close();
			rs.close();
			return true;
			 
		} catch (final SQLException ex){
			System.err.println("failed to check if user is in DB");
			ex.printStackTrace();
			return false;
		}
	}
}
