package ch.ethz.inf.dbproject.database;

import java.util.Date;
import java.util.List;
import ch.ethz.inf.dbproject.model.*;

public interface PersonDatastoreInterface {

	////
	//QUERY
	////
	
	/**
	 * @param personId
	 * @return if the personId is valid, a Person object representing the person
	 * 		   otherwise null
	 */
	Person getPersonForId(int personId);
	
	/**
	 * @param personId
	 * @return if the personId is valid, a list of cases for which the person is convicted
	 * 		   otherwise null
	 */
	List<CaseDetail> getCasesForWhichPersonIsConvicted(int personId);
	
	/**
	 * @param personId
	 * @return if the personId is valid, a list of cases in which the person is suspected
	 * 		   otherwise null
	 */
	List<CaseDetail> getCasesForWhichPersonIsSuspected(int personId);
	
	/**
	 * @param personId
	 * @return if the personId is valid, a list of notes that have been recorded on that person
	 */
	List<PersonNote> getPersonNotesForPerson(int personId);
	
	/**
	 * @param firstName
	 * @param lastName
	 * @return a list of persons whose names math the partial strings given as arguments
	 */
	List<Person> getPersonsForName(String firstName, String lastName);
	
	/**
	 * @param categoryName
	 * @return a list of persons that have been convicted for crimes of the given category
	 */
	List<Person> getPersonsForConvictionType(String categoryName);
	
	/**
	 * @param startDate
	 * @return a list of persons that have been convicted at the given date
	 */
	List<Person> getPersonsForConvictionDate(Datesql startDate);
	
	////
	//MODIFY
	////
	
	/**
	 * Adds a new personNote for a specific person to the DB. The note will be created with the date representing the current time.
	 * If the personId or the authorUsername refer to entities that don't exist, null is returned and the DB is not modified
	 * @param personId id of the person
	 * @param text the comment of the user
	 * @param authorUsername the user name
	 * @return if the parameters are valid, a PersonNote object representing the PersonNote just added to the DB
	 * 		   otherwise, null
	 */
	PersonNote addPersonNote(int personId, String text, String authorUsername);
	
}
