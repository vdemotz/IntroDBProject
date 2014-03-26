package ch.ethz.inf.dbproject.database;

import java.sql.Connection;
import java.util.List;

import ch.ethz.inf.dbproject.model.CaseDetail;
import ch.ethz.inf.dbproject.model.User;

public class UserDatastore implements UserDatastoreInterface {

	private Connection sqlConnection;

	public UserDatastore() {
		this.sqlConnection = MySQLConnection.getInstance().getConnection();
	}
	
	@Override
	public User getUserForUsernameAndPassword(String username, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CaseDetail> getCurrentCasesForUser(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User addUser(String username, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isUsernameAvailable(String username) {
		// TODO Auto-generated method stub
		return false;
	}

	
	
}
