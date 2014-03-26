package ch.ethz.inf.dbproject.database;

import java.util.List;
import java.util.Date;
import ch.ethz.inf.dbproject.model.CaseDetail;
import ch.ethz.inf.dbproject.model.CaseNote;


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
	List<CaseDetail> getCasesForDate(Date date);
	
	////
	//MODIFY
	////
	
	/**
	 * Adds a new caseNote for a specific case to the DB. The note will be created with the date representing the current time.
	 * If the caseId or the authorUsername refer to entities that don't exist, null is returned and the DB is not modified
	 * @param caseId the caseId of the CaseDetail
	 * @param text the comment of the user
	 * @param authorUsername the user name
	 * @return if the parameters are valid, a CaseNote object representing the CaseNote just added to the DB
	 * 		   otherwise, null is returned
	 */
	CaseNote addCaseNote(int caseId, String text, String authorUsername);
	
	//TODO: addCase
}
