package ch.ethz.inf.dbproject.database;

import java.util.List;
import ch.ethz.inf.dbproject.model.CaseDetail;
import ch.ethz.inf.dbproject.model.CaseNote;
import ch.ethz.inf.dbproject.model.Category;
import ch.ethz.inf.dbproject.model.CategorySummary;
import ch.ethz.inf.dbproject.model.Person;


public interface CaseDatastoreInterface {

	////
	//QUERY
	////
	
	/**
	 * @param id
	 * @return If caseId is valid, a CaseDetail object representing the Case with the given id it
	 *	       otherwise, null
	 */
	CaseDetail getCaseForId(int caseId);
	
	/**
	 * @return A list of all cases
	 */
	List<CaseDetail> getAllCases();
	
	/**
	 * @return A list of all open cases
	 */
	List<CaseDetail> getOpenCases();
	
	/**
	 * @return A list of all closed cases
	 */
	List<CaseDetail> getClosedCases();
	
	
	/**
	 * @return A list of the recent cases
	 */
	List<CaseDetail> getRecentCases();
	
	
	/**
	 * @return A list of the oldest unresolved cases
	 */
	List<CaseDetail> getOldestUnresolvedCases();
	
	/**
	 * 
	 * @param caseID
	 * @return if caseId is valid, a list of CaseNote objects representing the notes on that case
	 * 		   otherwise, null
	 */
	List<CaseNote> getCaseNotesForCase(int caseID);
	
	/**
	 * @param name
	 * @return a list of CaseDetail objects representing the cases for the category with the given name
	 */
	List<CaseDetail> getCasesForCategory(String name);
	
	/**
	 * @param date
	 * @return a list of cases for incidents that occurred on the given date
	 */
	List<CaseDetail> getCasesForDate(java.sql.Date date);
	
	/**
	 * @param date a string whose format is a prefix of yyyy-mm-dd
	 * @return a list of cases for incidents that occurred on the given date
	 * 		   if the string does not denote the prefix of any valid date, the empty list is returned
	 */
	List<CaseDetail> getCasesForDateLike(String date);
	
	/**
	 * @param caseId
	 * @return a list of persons if the caseId is valid,
	 * 		   otherwise null
	 */
	List<Person> getSuspectsForCase(int caseId);
	
	/**
	 * @param caseId
	 * @return a list of persons if the caseId is valid,
	 * 		   otherwise null
	 */
	List<Person> getConvictsForCase(int caseId);
	
	/**
	 * @param caseId
	 * @return a list of categories if the caseId is valid
	 * 		   otherwise null
	 */
	List<Category>getCategoriesForCase(int caseId);
	
	/**
	 * @return A list containing summary information about the cases of different categories
	 */
	List<CategorySummary>getCategorySummary();
	
	
	////
	//MODIFY
	////
	
	/**
	 * Adds a new caseNote for a specific case to the DB. The note will be created with the date representing the current time.
	 * If the caseId or the authorUsername refer to entities that don't exist, null is returned and the DB is not modified
	 * @param caseId the caseId of the CaseDetail
	 * @param text the comment of the user
	 * @param authorUsername the user name
	 * @return if the parameters are valid and the insertion succeeds, a CaseNote object representing the CaseNote just added to the DB
	 * 		   otherwise, null is returned
	 */
	CaseNote insertIntoCaseNote(int caseId, String text, String authorUsername);
	
	/**
	 * @param caseId the id of the case
	 * @param isOpen the new isOpen value for the case
	 * @return if the operation succeeds true, otherwise false
	 */
	boolean updateCaseIsOpen(int caseId, boolean isOpen);
	
	/**
	 * @param title
	 * @param city
	 * @param zipCode
	 * @param street
	 * @param date
	 * @param description
	 * @param authorUsername
	 * @return if the parameters are valid and insertion succeeds, an object representing the new CaseDetail
	 * 		   otherwise null
	 */
	CaseDetail insertIntoCaseDetail(String title, String city, String zipCode, String street, java.sql.Timestamp date, String description, String authorUsername);
	
	/**
	 * @param caseId
	 * @param personId
	 * @return true if successful, false otherwise
	 */
	boolean addSuspectToCase(int caseId, int personId);
	
	/**
	 * 
	 * @param caseId
	 * @param personId
	 * @param convictionId
	 * @return true if successful, false otherwise
	 */
	boolean addConvictToCase(int caseId, int personId, int convictionId);
	
	/**
	 * insert into table 'Category' a new entry
	 * @param name the name of the new category
	 * @return true if no problem, else false
	 */
	boolean insertIntoCategory(String name);

	/**
	 * insert into table 'CategoryForCase' a new entry
	 * @param name the name of the category
	 * @param caseId the id of the case
	 * @return true if no problem, else false
	 */
	boolean insertIntoCategoryForCase(String name, int caseId);

	/**
	 * return a Category for a specific name if its exists
	 * @param name the name of the category
	 * @return Category of that name, null if doesn't exists
	 */
	Category getCategoryForName(String name);
}
