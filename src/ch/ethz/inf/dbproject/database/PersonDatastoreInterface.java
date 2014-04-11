package ch.ethz.inf.dbproject.database;

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
	List<Person> getPersonsForConvictionDate(String startDate);
	
	/**
	 * @return a list of persons that are convicted for at least one crime
	 */
	List<Person> getAllConvictedPersons();
	
	/**
	 * @return a list of persons that are suspected for at least one crime
	 */
	List<Person> getAllSuspectedPersons();
	
	/**
	 * @return a list of all persons
	 */
	List<Person> getAllPersons();

	/**
	 * @return a list of all persons for a specific last name
	 */
	List<Person> getPersonsForLastName(String lastName);
	
	/**
	 * @return a list of all persons for a specific first name
	 */
	List<Person> getPersonsForFirstName(String firstName);
	
	/**
	 * provide a search in DB for a range of dates of birthdate
	 * @param startDate a valid String which represents a date yyyy-mm-dd
	 * @param endDate a valid String which represents a date yyyy-mm-dd
	 * @return a list of persons who have birthdate between start and end date
	 */
	List<Person> getPersonsForBirthdates(String startDate, String endDate);	
	
	/**
	 * provide a search in DB for a birthdate
	 * @param date a valid String which represents a date yyyy-mm-dd
	 * @return a list of persons who have birthdate at date
	 */
	List<Person> getPersonsForBirthdate(String date);	
	
	/**
	 * provide a search in DB for a range of dates of conviction
	 * @param startDate a valid String which represents a date yyyy-mm-dd
	 * @param endDate a valid String which represents a date yyyy-mm-dd
	 * @return a list of persons who have been convicted between start and end date
	 */
	List<Person> getPersonsForConvictionDates(String startDate, String endDate);
	
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

	
	/**
	 * Adds a new Person to the DB.
	 * @param firstName first name of the person
	 * @param lastName last name of the person
	 * @param date the birthdate of the person
	 * @return if parameters are valid, a Person object representing the Person just added
	 */
	Person addPerson(String firstName, String lastName, String date);

	/**
	 * Add a new suspicion entry
	 * @param caseId the case for which the person is suspected
	 * @param personId the person id of the person
	 * @return true parameters are valid and updated was made in database
	 */
	boolean setPersonSuspected(int caseId, int personId);
}
