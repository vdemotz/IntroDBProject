package ch.ethz.inf.dbproject.database;

import java.sql.*;
import java.util.List;
import java.sql.Timestamp;
import java.text.ParseException;

import ch.ethz.inf.dbproject.model.CaseDetail;
import ch.ethz.inf.dbproject.model.ModelObject;
import ch.ethz.inf.dbproject.model.Person;
import ch.ethz.inf.dbproject.model.PersonNote;

public class PersonDatastore implements PersonDatastoreInterface {

	////
	//Connection
	////
	private Connection sqlConnection;
	
	////
	// String for Prepared Statement
	////
	//particular cases for convicted person
	private String getCasesForWhichPersonIsConvictedString = "select cd.* from CaseDetail cd, Convicted c "+
			"where c.personId = ? and cd.caseId = c.caseId";
	//particular cases for suspected person
	private String getCasesForWhichPersonIsSuspectedString = "select cd.* from CaseDetail cd, Suspected s "+
			"where s.personId = ? and cd.caseId = s.caseId";
	//get person note for particular person
	private String getPersonNotesForPersonString = "select * from PersonNote where personId = ?";
	//get persons for a particular first name or last name
	private String getPersonsForNameString = "select * from Person where firstName like ? and lastName like ?";
	//persons for particular first name
	private String getPersonsForFirstNameString = "select * from Person where firstName like ?";
	//persons for particular first name
	private String getPersonsForLastNameString = "select * from Person where lastName like ?";
	//persons for particular conviction type
	private String getPersonsForConvictionTypeString = "select p.* from Convicted convicted, Person p, ConvictionType convictionType "+
			"where convicted.personId = p.personId and convicted.convictionId = convictionType.convictionId and "+
			"convictionType.categoryName = ? order by lastName, firstName";
	//persons for particular date
	private String getPersonsForConvictionDateString = "select person.* from Conviction conviction, Convicted convicted, Person person "+
			"where conviction.startDate like ? and conviction.convictionId = convicted.convictionId and "+
			"convicted.personId = person.personId order by lastName, firstName";
	//particular person for an Id
	private String getPersonForIdString = "select * from Person where personId =?";
	//all convicted persons
	private String getAllConvictedPersonsString = "select distinct p.* from Person p, Convicted c where p.personId = c.personId";
	//all suspected persons
	private String getAllSuspectedPersonsString = "select distinct p.* from Person p, Suspected s where p.personId = s.personId";
	//all persons
	private String getAllPersonsString = "select * from Person order by lastName desc, firstName desc";
	
	//add a personNote
	private String addPersonNoteString = "insert into PersonNote(PersonId, PersonNoteId, text, date, authorUsername) values(?, ?, ?, ?, ?)";
	//get the max Id for person notes for a particular person
	private String getMaxPersonNoteIdForPersonIdString = "select max(personNoteId) from PersonNote where personId = ?";
	
	//add a person
	private String addPersonString = "insert into Person(personId, firstName, lastName, birthdate) values(?, ?, ?, ?)";
	//get the max Id for person
	private String getMaxPersonIdString = "select max(PersonId) from Person";
	
	//set a person as suspected
	private String setPersonSuspectedString = "insert into Suspected(personId, caseId) values(?, ?)";
	
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

	////
	//Constructor
	////
	public PersonDatastore() {
		this.sqlConnection = MySQLConnection.getInstance().getConnection();
		try {
			prepareStatements();
		} catch (SQLException e){
			e.printStackTrace();
		}
	}

	private void prepareStatements() throws SQLException {
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
	}

	/**
	 * Executes a statement, and tries to instantiate a list of ModelObjects of the specified modelClass using the resultSet from the statement
	 * If the execution of the statement or instantiation raises an SQLException, null is returned.
	 * @param statement the configured statement to execute and get the results of
	 * @return a list of modelObjects representing the result of the execution of the statement
	 */
	private <T extends ModelObject> List<T> getResults(Class<T> modelClass, PreparedStatement statement)
	{
		 try {
			statement.execute();
			return ModelObject.getAllModelObjectsWithClassFromResultSet(modelClass, statement.getResultSet());
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
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
	public List<Person> getPersonsForName(String firstName, String lastName) {
		try{
			getPersonsForNameStatement.setString(1, firstName+"%");
			getPersonsForNameStatement.setString(2, lastName+"%");
		} catch (final SQLException ex){
			ex.printStackTrace();
			return null;
		}
		return getResults(Person.class, getPersonsForNameStatement);
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
