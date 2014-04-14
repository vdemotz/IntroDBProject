package ch.ethz.inf.dbproject.database;

import java.sql.*;
import java.util.Date;
import java.util.List;
import java.sql.Timestamp;
import java.text.ParseException;

import ch.ethz.inf.dbproject.model.CaseDetail;
import ch.ethz.inf.dbproject.model.ModelObject;
import ch.ethz.inf.dbproject.model.Person;
import ch.ethz.inf.dbproject.model.PersonNote;

public class PersonDatastore extends Datastore implements PersonDatastoreInterface {

	////
	// String for Prepared Statement
	////
	//particular cases for convicted person
	private static final String getCasesForWhichPersonIsConvictedString = "select cd.* from CaseDetail cd, Conviction c "+
			"where c.personId = ? and cd.caseId = c.caseId";
	//particular cases for suspected person
	private static final String getCasesForWhichPersonIsSuspectedString = "select cd.* from CaseDetail cd, Suspected s "+
			"where s.personId = ? and cd.caseId = s.caseId";
	//get person note for particular person
	private static final String getPersonNotesForPersonString = "select * from PersonNote where personId = ? order by date desc";
	//get persons for a particular first name or last name
	private static final String getPersonsForNameString = "select * from Person where firstName like ? and lastName like ?";
	//persons for particular first name
	private static final String getPersonsForFirstNameString = "select * from Person where firstName like ?";
	//persons for particular first name
	private static final String getPersonsForLastNameString = "select * from Person where lastName like ?";
	//persons for particular conviction type
	private static final String getPersonsForConvictionTypeString = "select person.* " +
													   "from Conviction conviction, Person person, CategoryForCase categoryForCase "+
													   "where conviction.personId = person.personId "+
													   "and conviction.caseId = categoryForCase.caseId " +
													   "and categoryForCase.categoryName = ? " +
													   "order by lastName, firstName";
	//persons for particular date of conviction
	private static final String getPersonsForConvictionDateString = "select person.* from Conviction conviction, Person person "+
			"where conviction.startDate like ? and "+
			"conviction.personId = person.personId order by lastName, firstName";
	//persons for particular dates range of conviction
	private static final String getPersonsForConvictionDatesString = "select person.* from Conviction conviction, Person person "+
			"where conviction.startDate between ? and ? and "+
			"conviction.personId = person.personId order by lastName, firstName";
	//person for particular birthdates range
	private static final String getPersonsForBirthdatesLikeString = "select * from person where birthdate between ? and ?";
	//person for particular birthdate range
	private static final String getPersonsForBirthdateString = "select * from person where birthdate like ?";
	//particular person for an Id
	private static final String getPersonForIdString = "select * from Person where personId =?";
	//all convicted persons
	private static final String getAllConvictedPersonsString = "select distinct p.* from Person p, Conviction c where p.personId = c.personId";
	//all suspected persons
	private static final String getAllSuspectedPersonsString = "select distinct p.* from Person p, Suspected s where p.personId = s.personId";
	//all persons
	private static final String getAllPersonsString = "select * from Person order by lastName desc, firstName desc";
	
	//add a personNote
	private static final String addPersonNoteString = "insert into PersonNote(PersonId, PersonNoteId, text, date, authorUsername) values(?, ?, ?, ?, ?)";
	//get the max Id for person notes for a particular person
	private static final String getMaxPersonNoteIdForPersonIdString = "select max(personNoteId) from PersonNote where personId = ?";
	
	//add a person
	private static final String addPersonString = "insert into Person(personId, firstName, lastName, birthdate) values(?, ?, ?, ?)";
	//get the max Id for person
	private static final String getMaxPersonIdString = "select max(PersonId) from Person";
	
	//set a person as suspected
	private static final String setPersonSuspectedString = "insert into Suspected(personId, caseId) values(?, ?)";
	
	////
	//Prepared Statements
	////
	private PreparedStatement getCasesForWhichPersonIsConvictedStatement;
	private PreparedStatement getCasesForWhichPersonIsSuspectedStatement;
	private PreparedStatement getPersonNotesForPersonStatement;
	private PreparedStatement getPersonsForNameStatement;
	private PreparedStatement getPersonsForConvictionTypeStatement;
	private PreparedStatement getPersonsForConvictionDateStatement;
	private PreparedStatement getPersonForIdStatement;
	private PreparedStatement getAllConvictedPersonsStatement;
	private PreparedStatement getAllSuspectedPersonsStatement;
	private PreparedStatement addPersonNoteStatement;
	private PreparedStatement getMaxPersonNoteIdForPersonIdStatement;
	private PreparedStatement addPersonStatement;
	private PreparedStatement getMaxPersonIdStatement;
	private PreparedStatement setPersonSuspectedStatement;
	private PreparedStatement getAllPersonsStatement;
	private PreparedStatement getPersonsForLastNameStatement;
	private PreparedStatement getPersonsForFirstNameStatement;
	private PreparedStatement getPersonsForConvictionDatesStatement;
	private PreparedStatement getPersonsForBirthdateLikeStatement;
	private PreparedStatement getPersonsForBirthdatesStatement;

	////
	//Constructor
	////

	@Override
	protected void prepareStatements() throws SQLException {
		getCasesForWhichPersonIsConvictedStatement = sqlConnection.prepareStatement(getCasesForWhichPersonIsConvictedString);
		getCasesForWhichPersonIsSuspectedStatement = sqlConnection.prepareStatement(getCasesForWhichPersonIsSuspectedString);
		getPersonNotesForPersonStatement = sqlConnection.prepareStatement(getPersonNotesForPersonString);
		getPersonsForNameStatement = sqlConnection.prepareStatement(getPersonsForNameString);
		getPersonsForConvictionTypeStatement = sqlConnection.prepareStatement(getPersonsForConvictionTypeString);
		getPersonsForConvictionDateStatement = sqlConnection.prepareStatement(getPersonsForConvictionDateString);
		getPersonForIdStatement = sqlConnection.prepareStatement(getPersonForIdString);
		getAllConvictedPersonsStatement = sqlConnection.prepareStatement(getAllConvictedPersonsString);
		getAllSuspectedPersonsStatement = sqlConnection.prepareStatement(getAllSuspectedPersonsString);
		addPersonNoteStatement = sqlConnection.prepareStatement(addPersonNoteString);
		getMaxPersonNoteIdForPersonIdStatement = sqlConnection.prepareStatement(getMaxPersonNoteIdForPersonIdString);
		addPersonStatement = sqlConnection.prepareStatement(addPersonString);
		getMaxPersonIdStatement = sqlConnection.prepareStatement(getMaxPersonIdString);
		setPersonSuspectedStatement = sqlConnection.prepareStatement(setPersonSuspectedString);
		getAllPersonsStatement = sqlConnection.prepareStatement(getAllPersonsString);
		getPersonsForFirstNameStatement = sqlConnection.prepareStatement(getPersonsForFirstNameString);
		getPersonsForLastNameStatement = sqlConnection.prepareStatement(getPersonsForLastNameString);
		getPersonsForConvictionDatesStatement = sqlConnection.prepareStatement(getPersonsForConvictionDatesString);
		getPersonsForBirthdateLikeStatement = sqlConnection.prepareStatement(getPersonsForBirthdateString);
		getPersonsForBirthdatesStatement = sqlConnection.prepareStatement(getPersonsForBirthdatesLikeString);
	}
	
	////
	//Return type List<CaseDetail>
	////
	
	@Override
	public List<CaseDetail> getCasesForWhichPersonIsConvicted(int personId) {
		try{
			getCasesForWhichPersonIsConvictedStatement.setInt(1, personId);
		} catch (final SQLException ex){
			ex.printStackTrace();
			return null;
		}
		return getResults(CaseDetail.class, getCasesForWhichPersonIsConvictedStatement);
	}

	@Override
	public List<CaseDetail> getCasesForWhichPersonIsSuspected(int personId) {
		try{
			getCasesForWhichPersonIsSuspectedStatement.setInt(1, personId);
		} catch (final SQLException ex){
			ex.printStackTrace();
			return null;
		}
		return getResults(CaseDetail.class, getCasesForWhichPersonIsSuspectedStatement);
	}
	
	////
	//Return type List<PersonNote>
	////

	@Override
	public List<PersonNote> getPersonNotesForPerson(int personId) {
		try{
			getPersonNotesForPersonStatement.setInt(1, personId);
		} catch (final SQLException ex){
			ex.printStackTrace();
			return null;
		}
		return getResults(PersonNote.class, getPersonNotesForPersonStatement);
	}
	
	////
	//Return type List<Person>
	////
	
	@Override
	public List<Person> getPersonsForBirthdates(java.util.Date startDate, java.util.Date endDate){
		try{
			getPersonsForBirthdateLikeStatement.setDate(1, new java.sql.Date(startDate.getTime()));
			getPersonsForBirthdateLikeStatement.setDate(2, new java.sql.Date(endDate.getTime()));
		} catch (Exception ex){
			ex.printStackTrace();
			return null;
		}
		return getResults(Person.class, getPersonsForBirthdatesStatement);
	}
	
	@Override
	public List<Person> getPersonsForBirthdate(String date){
		try{
			getPersonsForBirthdateLikeStatement.setString(1, date+"%");
		} catch (Exception ex){
			ex.printStackTrace();
			return null;
		}
		return getResults(Person.class, getPersonsForBirthdateLikeStatement);
	}
	
	@Override
	public List<Person> getPersonsForConvictionDates(java.util.Date startDate, java.util.Date endDate){
		try{
			getPersonsForConvictionDatesStatement.setDate(1, new java.sql.Date(startDate.getTime()));
			getPersonsForConvictionDatesStatement.setDate(2, new java.sql.Date(endDate.getTime()));
		} catch (Exception ex){
			ex.printStackTrace();
			return null;
		}
		return getResults(Person.class, getPersonsForConvictionDatesStatement);
	}

	@Override
	public List<Person> getPersonsForName(String firstName, String lastName) {
		try{
			
			if (lastName.isEmpty() && firstName.isEmpty()){
				return this.getAllPersons();
			} else if (lastName.isEmpty()){
				return this.getPersonsForFirstName(firstName);
			} else if (firstName.isEmpty()){
				return this.getPersonsForLastName(lastName);
			} else {
				getPersonsForNameStatement.setString(1, firstName+"%");
				getPersonsForNameStatement.setString(2, lastName+"%");
				return getResults(Person.class, getPersonsForNameStatement);
			}
			
		} catch (final SQLException ex){
			ex.printStackTrace();
			return null;
		}
	}
	
	@Override
	public List<Person> getPersonsForLastName(String lastName) {
		try{
			getPersonsForLastNameStatement.setString(1, lastName+"%");
		} catch (final SQLException ex){
			ex.printStackTrace();
			return null;
		}
		return getResults(Person.class, getPersonsForLastNameStatement);
	}
	
	@Override
	public List<Person> getPersonsForFirstName(String firstName) {
		try{
			getPersonsForFirstNameStatement.setString(1, firstName+"%");
		} catch (final SQLException ex){
			ex.printStackTrace();
			return null;
		}
		return getResults(Person.class, getPersonsForFirstNameStatement);
	}

	@Override
	public List<Person> getPersonsForConvictionType(String categoryName) {
		try{
			getPersonsForConvictionTypeStatement.setString(1, categoryName);
		} catch (final SQLException ex){
			ex.printStackTrace();
			return null;
		}
		return getResults(Person.class, getPersonsForConvictionTypeStatement);
	}

	@Override
	public List<Person> getPersonsForConvictionDate(String startDate) {
		try{
			getPersonsForConvictionDateStatement.setString(1, startDate + "%");
		} catch (final SQLException ex){
			ex.printStackTrace();
			return null;
		}
		return getResults(Person.class, getPersonsForConvictionDateStatement);
	}
	
	@Override
	public List<Person> getAllConvictedPersons() {
		return getResults(Person.class, getAllConvictedPersonsStatement);
	}
	
	@Override
	public List<Person> getAllSuspectedPersons() {
		return getResults(Person.class, getAllSuspectedPersonsStatement);
	}
	
	@Override
	public List<Person> getAllPersons() {
		return getResults(Person.class, getAllPersonsStatement);
	}
	
	////
	//Return type person
	////
	
	@Override
	public Person getPersonForId(int personId) {
		try{
			getPersonForIdStatement.setInt(1, personId);
			ResultSet rs = getPersonForIdStatement.executeQuery();
			if (!rs.first()){ return null; }
			return new Person(rs);
		} catch (final SQLException ex){
			ex.printStackTrace();
			return null;
		}
	}
	
	////
	//Add
	////
	
	@Override
	public PersonNote addPersonNote(int personId, String text, String authorUsername) {
		synchronized (this.getClass()){
			try{
				int personNoteId = getMaxPersonNoteIdForPersonId(personId);
				Timestamp t = new Timestamp(new java.util.Date().getTime());
				addPersonNoteStatement.setInt(1, personId);
				addPersonNoteStatement.setInt(2, personNoteId);
				addPersonNoteStatement.setString(3, text);
				addPersonNoteStatement.setTimestamp(4, t);
				addPersonNoteStatement.setString(5, authorUsername);
				addPersonNoteStatement.execute();
				return new PersonNote(personId, personNoteId, text, t, authorUsername);
			} catch (final SQLException ex){
				ex.printStackTrace();
				return null;
			}
		}
	}
	
	private int getMaxPersonNoteIdForPersonId(int personId) {
		try{
			getMaxPersonNoteIdForPersonIdStatement.setInt(1, personId);
			ResultSet rs = getMaxPersonNoteIdForPersonIdStatement.executeQuery();
			if (!rs.first()){ return 0; }
			return (rs.getInt("max(personNoteId)")+1);
		} catch (final SQLException ex){
			ex.printStackTrace();
			return -1;
		}
	}
	
	@Override
	public Person addPerson(String firstName, String lastName, String date){
		synchronized (this.getClass()){
			try{
				int id = this.getMaxPersonId();
				addPersonStatement.setInt(1, id);
				addPersonStatement.setString(2, firstName);
				addPersonStatement.setString(3, lastName);
				addPersonStatement.setString(4, date);
				addPersonStatement.execute();
				try { return new Person(id, firstName, lastName, date);
				} catch (ParseException e) {
					e.printStackTrace();
					return null;
				}
			} catch (final SQLException ex){
				ex.printStackTrace();
				return null;
			}
		}
	}
	
	@Override
	public boolean setPersonSuspected(int caseId, int personId){
		try{
			setPersonSuspectedStatement.setInt(1, personId);
			setPersonSuspectedStatement.setInt(2, caseId);
			setPersonSuspectedStatement.execute();
			return true;
		} catch (final SQLException ex){
			ex.printStackTrace();
			return false;
		}
	}
	
	private int getMaxPersonId() {
		try{
			ResultSet rs = getMaxPersonIdStatement.executeQuery();
			if (!rs.first()){ return 0; }
			return (rs.getInt("max(personId)")+1);
		} catch (final SQLException ex){
			ex.printStackTrace();
			return -1;
		}
	}

}
