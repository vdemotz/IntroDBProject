package ch.ethz.inf.dbproject.database;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import ch.ethz.inf.dbproject.model.CaseDetail;
import ch.ethz.inf.dbproject.model.CaseNote;
import ch.ethz.inf.dbproject.model.Category;
import ch.ethz.inf.dbproject.model.CategorySummary;
import ch.ethz.inf.dbproject.model.Person;
import ch.ethz.inf.dbproject.model.PersonNote;
import ch.ethz.inf.dbproject.model.User;

/**
 * This class is be the interface between the web application
 * and the database.
 */

public final class DatastoreInterface implements CaseDatastoreInterface, PersonDatastoreInterface, UserDatastoreInterface {

	private CaseDatastoreInterface caseDatastore = new CaseDatastore();
	private PersonDatastoreInterface personDatastore = new PersonDatastore();
	private UserDatastoreInterface userDatastore = new UserDatastore();
	
	////
	//USER
	////
	
	@Override
	public boolean isUsernameAvailable(String username) {
		return userDatastore.isUsernameAvailable(username);
	}
	
	@Override
	public User getUserForUsernameAndPassword(String username, String password) {
		return userDatastore.getUserForUsernameAndPassword(username, password);
	}
	
	@Override
	public List<CaseDetail> getCurrentCasesForUser(String username) {
		return userDatastore.getCurrentCasesForUser(username);
	}
	
	@Override
	public User addUser(String username, String password, String lastName, String firstName) {
		return userDatastore.addUser(username, password, lastName, firstName);
	}
	
	////
	//PERSON
	////
	
	@Override
	public List<Person> getAllConvictedPersons() {
		return personDatastore.getAllConvictedPersons();
	}
	
	@Override
	public List<Person> getAllSuspectedPersons() {
		return personDatastore.getAllSuspectedPersons();
	}
	
	
	@Override
	public List<CaseDetail> getCasesForWhichPersonIsConvicted(int personId) {
		return personDatastore.getCasesForWhichPersonIsConvicted(personId);
	}
	
	@Override
	public List<CaseDetail> getCasesForWhichPersonIsSuspected(int personId) {
		return personDatastore.getCasesForWhichPersonIsSuspected(personId);
	}
	
	@Override
	public List<PersonNote> getPersonNotesForPerson(int personId) {
		return personDatastore.getPersonNotesForPerson(personId);
	}
	
	@Override
	public List<Person> getPersonsForName(String firstName, String lastName) {
		return personDatastore.getPersonsForName(firstName, lastName);
	}
	
	@Override
	public List<Person> getPersonsForConvictionType(String categoryName) {
		return personDatastore.getPersonsForConvictionType(categoryName);
	}
	
	@Override
	public List<Person> getPersonsForConvictionDate(Date startDate) {
		return personDatastore.getPersonsForConvictionDate(startDate);
	}
	
	@Override
	public Person getPersonForId(int personId) {
		return personDatastore.getPersonForId(personId);
	}
	
	@Override
	public PersonNote addPersonNote(int personId, String text, String authorUsername) {
		return personDatastore.addPersonNote(personId, text, authorUsername);
	}
	
	////
	//CASE
	////
	
	@Override
	public CaseDetail getCaseForId(int caseId) {
		return caseDatastore.getCaseForId(caseId);
	}
	
	@Override
	public List<CaseDetail> getAllCases() {
		return caseDatastore.getAllCases();
	}
	
	@Override
	public List<CaseNote> getCaseNotesForCase(int caseID) {
		return caseDatastore.getCaseNotesForCase(caseID);
	}
	
	@Override
	public List<CaseDetail> getCasesForCategory(String name) {
		return caseDatastore.getCasesForCategory(name);
	}
	
	@Override
	public List<CaseDetail> getCasesForDate(java.sql.Date date) {
		return caseDatastore.getCasesForDate(date);
	}
	
	@Override
	public List<CaseDetail> getCasesForDateLike(String date) {
		return caseDatastore.getCasesForDateLike(date);
	}
	
	@Override
	public List<CaseDetail> getOpenCases() {
		return caseDatastore.getOpenCases();
	}
	
	@Override
	public List<CaseDetail> getOldestUnresolvedCases() {
		return caseDatastore.getOldestUnresolvedCases();
	}

	@Override
	public List<CaseDetail> getClosedCases() {
		return caseDatastore.getClosedCases();
	}

	@Override
	public List<CaseDetail> getRecentCases() {
		return caseDatastore.getRecentCases();
	}

	@Override
	public List<Person> getSuspectsForCase(int caseId) {
		return caseDatastore.getSuspectsForCase(caseId);
	}

	@Override
	public List<Person> getConvictsForCase(int caseId) {
		return caseDatastore.getConvictsForCase(caseId);
	}

	@Override
	public List<Category> getCategoriesForCase(int caseId) {
		return caseDatastore.getCategoriesForCase(caseId);
	}

	@Override
	public List<CategorySummary> getCategorySummary() {
		return caseDatastore.getCategorySummary();
	}

	@Override
	public CaseNote insertIntoCaseNote(int caseId, String text, String authorUsername) {
		return caseDatastore.insertIntoCaseNote(caseId, text, authorUsername);
	}

	@Override
	public boolean updateCaseIsOpen(int caseId, boolean isOpen) {
		return caseDatastore.updateCaseIsOpen(caseId, isOpen);
	}

	@Override
	public CaseDetail insertIntoCaseDetail(String title, String city, String zipCode, String street, Timestamp date, String description, String authorUsername) {
		return caseDatastore.insertIntoCaseDetail(title, city, zipCode, street, date, description, authorUsername);
	}

	@Override
	public boolean addSuspectToCase(int caseId, int personId) {
		return caseDatastore.addSuspectToCase(caseId, personId);
	}

	@Override
	public boolean addConvictToCase(int caseId, int personId, int convictionId) {
		return caseDatastore.addConvictToCase(caseId, personId, convictionId);
	}


	
}
