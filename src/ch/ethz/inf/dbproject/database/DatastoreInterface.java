package ch.ethz.inf.dbproject.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import ch.ethz.inf.dbproject.model.CaseDetail;
import ch.ethz.inf.dbproject.model.CaseNote;
import ch.ethz.inf.dbproject.model.Person;
import ch.ethz.inf.dbproject.model.PersonNote;
import ch.ethz.inf.dbproject.model.User;

/**
 * This class is be the interface between the web application
 * and the database.
 */

public final class DatastoreInterface implements CaseDatastoreInteface, PersonDatastoreInteface, UserDatastoreInteface {

	private CaseDatastoreInteface caseDatastore = new CaseDatastore();
	private PersonDatastoreInteface personDatastore = new PersonDatastore();
	private UserDatastoreInteface userDatastore = new UserDatastore();
	
	////
	//USER
	////
	
	@Override
	public boolean isUsernameAvailable(String username) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public User getUserForUsernameAndPassword(String username, String password) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<CaseDetail> getCurrentCasesForUser(String username) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public User addUser(String username, String password) {
		// TODO Auto-generated method stub
		return null;
	}
	
	////
	//PERSON
	////
	
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
	
	@Override
	public PersonNote addPersonNote(int personId, String text,
			String authorUsername) {
		// TODO Auto-generated method stub
		return null;
	}
	
	////
	//CASE
	////
	
	@Override
	public CaseDetail getCaseForId(int id) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<CaseDetail> getAllCases() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<CaseNote> getCaseNotesForCase(int caseID) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<CaseDetail> getCasesForCategory(String name) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<CaseDetail> getCasesForDate(Date date) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public CaseNote addCaseNote(int caseId, String text, String authorUsername) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<CaseDetail> getOpenCases() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<CaseDetail> getOldestUnresolvedCases() {
		// TODO Auto-generated method stub
		return null;
	}





	////
	//LEGACY
	////
	
	
	/*
	public final Case getCaseById(final int id) {
		//get a case by its id
		try {
			final Statement stmt = this.sqlConnection.createStatement();
			final ResultSet rs = stmt.executeQuery("SELECT * FROM CaseDetail WHERE caseID = "+id);
			rs.first();	// set the pointer to the first and, hopefully, first case
			
			//rs.close();
			//stmt.close();
			// TODO Decide if we close or not this ResultSet and Statement or not
			
			return new Case(rs);
			
		} catch (final SQLException ex) {
			System.err.println("No case with this ID"); //For tests purposes
			ex.printStackTrace();
			return null;			
		}
	}*/
	
	/*
	public final List<Case> getAllCases() {
		//get all cases from database
		try {
			final Statement stmt = this.sqlConnection.createStatement();
			final ResultSet rs = stmt.executeQuery("SELECT * FROM CaseDetail");
			final List<Case> cases = new ArrayList<Case>(); 
			
			while (rs.next()) {
				cases.add(new Case(rs));
			}
			rs.close();
			stmt.close();
			return cases;
			
		} catch (final SQLException ex) {
			ex.printStackTrace();
			return null;			
		}
	}
	*/
	
	/*
	public final List<Case> getProjectsByCategory(String c) {
		//get cases by category
		try {
			final Statement stmt = this.sqlConnection.createStatement();
			final ResultSet rs = stmt.executeQuery("SELECT * FROM CaseDetail");
				// TODO change the query
			final List<Case> cases = new ArrayList<Case>(); 
			while (rs.next()) {
				cases.add(new Case(rs));
			}
			
			rs.close();
			stmt.close();

			return cases;
			
		} catch (final SQLException ex) {
			System.err.println("No case in this category");  // For tests purposes
			ex.printStackTrace();
			return null;			
		}
	}
	
	public final User getUserByUsernameAndPassword(String n, String p){
		//get user by username and password
		try {
			User usr;
			final Statement stmt = this.sqlConnection.createStatement();
			final ResultSet rs = stmt.executeQuery("SELECT * FROM User WHERE username = '"+n+"' and password = '"+p+"'");
			rs.first();
			
			usr = new User(rs);		
			rs.close();	
			stmt.close();	//close ResultSet and Statement
			
			return usr;
			
		} catch (final SQLException ex) {
			System.err.println("No user found"); // For tests purposes. Not a problem the method return null!!!
			return null;			
		}
	}
	
	public final List<CaseNote> getCaseNoteByCase(int caseID){
		// TODO better query
		// for the moment return all comments in DB
		try {

			final Statement stmt = this.sqlConnection.createStatement();
			final ResultSet rs = stmt.executeQuery("SELECT * FROM CaseNote");
			
			final List<CaseNote> cn = new ArrayList<CaseNote>(); 
			while (rs.next()) {
				cn.add(new CaseNote(rs));
			}
			rs.close();
			stmt.close();
			return cn;
			
		} catch (final SQLException ex) {
			System.err.println("No comment found"); // For tests purposes. Not a problem the method return null!!!
			return null;			
		}
	}
	
	public final int addCaseNote(int caseId, String text, java.util.Date date, String authorUSR){
		// Insert a comment in the DB
		// TODO update to CaseNote and PersonNote
		try {
			System.err.println(date.toString());

			final Statement stmt = this.sqlConnection.createStatement();
			
			ResultSet rs = stmt.executeQuery("SELECT * FROM CaseNote WHERE caseNoteId = LAST_INSERT_ID()");
			rs.first();
			return new CaseNote(rs).getCaseNoteId();
			
			
		} catch (final SQLException ex) {
			ex.printStackTrace();
			System.err.println("Case Note not added");
			return -1;
		}
	}*/
	
}
