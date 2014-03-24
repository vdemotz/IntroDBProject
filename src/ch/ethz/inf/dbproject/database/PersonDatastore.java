package ch.ethz.inf.dbproject.database;

import java.sql.Connection;
import java.util.Date;
import java.util.List;

import ch.ethz.inf.dbproject.model.CaseDetail;
import ch.ethz.inf.dbproject.model.Person;
import ch.ethz.inf.dbproject.model.PersonNote;

public class PersonDatastore implements PersonDatastoreInteface {

	private Connection sqlConnection;

	public PersonDatastore() {
		this.sqlConnection = MySQLConnection.getInstance().getConnection();
	}
	
	@Override
	public List<CaseDetail> getCasesForWhichPersonIsConvicted(int personId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CaseDetail> getCasesForWhichPersonIsSuspected(int personId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PersonNote> getPersonNotesForPerson(int personId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Person> getPersonsForName(String firstName, String lastName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Person> getPersonsForConvictionType(String categoryName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Person> getPersonsForConvictionDate(Date startDate) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Person getPersonForId(int personId) {
		// TODO Auto-generated method stub
		return null;
	}


	
	/**
	* - initialize all fields but caseNoteId with given attributes
	* - connect to database, create a CaseNote (via addCaseNote) in it and have it return an unique ID
	* - initialize the field caseNoteId, consistent with the DB

	* - the creation of a case note in java is always consistent with the DB
	* - we don't need (yet) to iterate through all the existing caseNoteId's
	* - we don't have to create separately the CaseNote in the DB
	*/
	
	@Override
	public PersonNote addPersonNote(int personId, String text,
			String authorUsername) {
		// TODO Auto-generated method stub
		return null;
	}


}
