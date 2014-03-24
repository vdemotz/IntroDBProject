package ch.ethz.inf.dbproject.database;

import java.util.List;

import ch.ethz.inf.dbproject.model.CaseDetail;
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
	
	/**
	 * Given a username, returns a list of recently created and commented on cases, ordered by descending recentness.
	 * If the username doesn't exits, null is returned. If the user has no Cases, the empty list is returned
	 * @param username
	 * @return the list of current cases if applicable
	 */
	List<CaseDetail> getCurrentCasesForUser(String username);
	
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
	
	User addUser(String username, String password);
}
