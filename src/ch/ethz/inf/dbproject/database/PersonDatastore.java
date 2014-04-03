package ch.ethz.inf.dbproject.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;
import ch.ethz.inf.dbproject.model.CaseDetail;
import ch.ethz.inf.dbproject.model.ModelObject;
import ch.ethz.inf.dbproject.model.Person;
import ch.ethz.inf.dbproject.model.PersonNote;
import ch.ethz.inf.dbproject.model.User;

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
	private String getPersonsForNameString = "select * from Person where firstName like ? or lastName like ? ";
	//persons for particular conviction type
	private String getPersonsForConvictionTypeString = "select p.* from Convicted convicted, Person p, ConvictionType convictionType "+
			"where convicted.personId = p.personId and convicted.convictionId = convictionType.convictionId and "+
			"convictionType.categoryName = ?";
	//persons for particular date
	private String getPersonsForConvictionDateString = "select person.* from Conviction conviction, Convicted convicted, Person person "+
			"where conviction.startDate = ? and conviction.convictionId = convicted.convictionId and "+
			"convicted.personId = person.personId";
	//particular person for an Id
	private String getPersonForIdString = "select * from Person where personId =?";
	//all convicted persons
	private String getAllConvictedPersonsString = "select p.* from Person p, Convicted c where p.personId = c.personId";
	//all suspected persons
	private String getAllSuspectedPersonsString = "select p.* from Person p, Suspected s where p.personId = s.personId";
	
	//add a person
	private String addPersonNoteString = "insert into PersonNote(PersonId, PersonNoteId, text, date, authorUsername) values(?, ?, ?, ?, ?)";
	//get the max Id for person notes for a particular person
	private String getMaxPersonNoteIdForPersonIdString = "select max(personNoteId) from PersonNote where personId = ?";
	
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
	
	private Timestamp time;

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
		time = new Timestamp(0);
		
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
			getPersonsForNameStatement.setString(1, firstName);
			getPersonsForNameStatement.setString(2, lastName);
		} catch (final SQLException ex){
			ex.printStackTrace();
			return null;
		}
		return getResults(Person.class, getPersonsForNameStatement);
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
	public List<Person> getPersonsForConvictionDate(Date startDate) {
		try{
			getPersonsForConvictionDateStatement.setString(1, startDate.toString());
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
		try{
			int personNoteId = getMaxPersonNoteIdForPersonId(personId);
			Timestamp t = new Timestamp(time.getTime());
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
}
