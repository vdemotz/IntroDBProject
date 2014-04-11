package ch.ethz.inf.dbproject.database;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import ch.ethz.inf.dbproject.model.CaseDetail;
import ch.ethz.inf.dbproject.model.CaseNote;
import ch.ethz.inf.dbproject.model.Category;
import ch.ethz.inf.dbproject.model.CategorySummary;
import ch.ethz.inf.dbproject.model.Conviction;
import ch.ethz.inf.dbproject.model.Person;
import ch.ethz.inf.dbproject.model.PersonNote;
import ch.ethz.inf.dbproject.model.StatsNode;
import ch.ethz.inf.dbproject.model.User;

/**
 * This class is be the interface between the web application
 * and the database.
 */

public final class DatastoreInterface implements CaseDatastoreInterface, PersonDatastoreInterface, UserDatastoreInterface, ConvictionDatastoreInterface, StatisticsDatastoreInterface {

	private CaseDatastoreInterface caseDatastore = new CaseDatastore();
	private PersonDatastoreInterface personDatastore = new PersonDatastore();
	private UserDatastoreInterface userDatastore = new UserDatastore();
	private ConvictionDatastoreInterface convictionDatastore = new ConvictionDatastore();
	private StatisticsDatastoreInterface statisticsDatastore = new StatisticsDatastore();
	
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
	public List<Person> getPersonsForConvictionType(String categoryName) {
		return personDatastore.getPersonsForConvictionType(categoryName);
	}
	
	@Override
	public List<Person> getPersonsForConvictionDate(String startDate) {
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
	
	@Override
	public Person addPerson(String firstName, String lastName, String date) {
		return personDatastore.addPerson(firstName, lastName, date);
	}
	
	@Override
	public boolean setPersonSuspected(int caseId, int personId) {
		return personDatastore.setPersonSuspected(caseId, personId);
	}
	
	@Override
	public List<Person> getAllPersons() {
		return personDatastore.getAllPersons();
	}
	
	@Override
	public List<Person> getPersonsForLastName(String lastName) {
		return personDatastore.getPersonsForLastName(lastName);
	}

	@Override
	public List<Person> getPersonsForFirstName(String firstName) {
		return personDatastore.getPersonsForFirstName(firstName);
	}
	
	@Override
	public List<Person> getPersonsForName(String firstName, String lastName) {
		return personDatastore.getPersonsForName(firstName, lastName);
	}

	@Override
	public List<Person> getPersonsForBirthdates(java.util.Date startDate, java.util.Date endDate) {
		return personDatastore.getPersonsForBirthdates(startDate, endDate);
	}

	@Override
	public List<Person> getPersonsForBirthdate(String date) {
		return personDatastore.getPersonsForBirthdate(date);
	}

	@Override
	public List<Person> getPersonsForConvictionDates(java.util.Date startDate, java.util.Date endDate) {
		return personDatastore.getPersonsForConvictionDates(startDate, endDate);
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
	public List<CaseDetail> getCasesForDate(java.util.Date date) {
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
	public CaseDetail insertIntoCaseDetail(String title, String city, String zipCode, String street, java.util.Date date, String description, String authorUsername) {
		return caseDatastore.insertIntoCaseDetail(title, city, zipCode, street, date, description, authorUsername);
	}

	@Override
	public boolean addSuspectToCase(int caseId, int personId) {
		return caseDatastore.addSuspectToCase(caseId, personId);
	}
	
	@Override
	public boolean insertIntoCategory(String name) {
		return caseDatastore.insertIntoCategory(name);
	}

	@Override
	public boolean insertIntoCategoryForCase(String name, int caseId) {
		return caseDatastore.insertIntoCategoryForCase(name, caseId);
	}

	@Override
	public Category getCategoryForName(String name) {
		return caseDatastore.getCategoryForName(name);
	}
	
	@Override
	public List<Category> getAllCategories() {
		return caseDatastore.getAllCategories();
	}
	
	@Override
	public boolean deleteCategoryForCaseIdAndCategory(int caseId, String categoryName) {
		return caseDatastore.deleteCategoryForCaseIdAndCategory(caseId, categoryName);
	}
	
	@Override
	public List<CaseDetail> getCasesForDates(Date startDate, Date endDate) {
		return caseDatastore.getCasesForDates(startDate, endDate);
	}
	
	/////
	//CONVICTION
	/////

	@Override
	public Conviction getConvictionForId(int convictionId) {
		return convictionDatastore.getConvictionForId(convictionId);
	}

	@Override
	public Conviction insertIntoConviction(int personId, Integer caseId, Date startDate, Date endDate) {
		return convictionDatastore.insertIntoConviction(personId, caseId, startDate, endDate);
	}
	
	////
	//STATISTICS
	////

	@Override
	public List<StatsNode> getCasesPerCity() {
		return statisticsDatastore.getCasesPerCity();
	}

	@Override
	public List<StatsNode> getCasesPerMonth() {
		return statisticsDatastore.getCasesPerMonth();
	}

	@Override
	public List<StatsNode> getConvictionsPerMonth() {
		return statisticsDatastore.getConvictionsPerMonth();
	}

	@Override
	public List<StatsNode> getConvictionsPerCity() {
		return statisticsDatastore.getConvictionsPerCity();
	}

	@Override
	public List<StatsNode> getConvictionsPerCategory() {
		return statisticsDatastore.getConvictionsPerCategory();
	}

	@Override
	public List<StatsNode> getNumberNotesPerUser() {
		return statisticsDatastore.getNumberNotesPerUser();
	}

	@Override
	public List<StatsNode> getMostActiveCategoriesForUser(String username) {
		return statisticsDatastore.getMostActiveCategoriesForUser(username);
	}
}
