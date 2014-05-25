package ch.ethz.inf.dbproject.database;

import ch.ethz.inf.dbproject.model.User;


public interface UserDatastoreInterface {

	////
	//QUERY
	////
	
	/**
	 * @param username
	 * @return false if a user with such a username already exists, true otherwise
	 */
	boolean isUsernameAvailable(String username);
	
	/**
	 * If there is a user with the given name and password, returns a User object representing that user, and null otherwise.
	 * @param username
	 * @param password
	 * @return the user if present
	 */
	User getUserForUsernameAndPassword(String username, String password);
	
	////
	//MODIFY
	////
	
	/**
	 * Given a new username, and a valid password, returns a User object representing the new user.
	 * If some of the parameters are invalid, null is returned and no changes are done to the DB.
	 * @param username
	 * @param password
	 * @return the new user if arguments are valid
	 */
	
	User addUser(String username, String password, String lastName, String firstName);
}
