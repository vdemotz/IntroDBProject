package ch.ethz.inf.dbproject.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


import ch.ethz.inf.dbproject.database.MySQLConnection;

/**
 * This class should be the interface between the web application
 * and the database. Keeping all the data-access methods here
 * will be very helpful for part 2 of the project.
 */

	// TODO Decide if we use statements directly or pre-compiled statements

public final class DatastoreInterface {

	@SuppressWarnings("unused")
	private Connection sqlConnection;

	public DatastoreInterface() {
		this.sqlConnection = MySQLConnection.getInstance().getConnection();
	}
	
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
	}
	
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
			final Statement stmt = this.sqlConnection.createStatement();
			stmt.execute("INSERT INTO CaseNote(caseID, text, date, authorUsername)"+
			"VALUES("+caseId+",'"+text+"','2012-12-12 00:00:00','"+authorUSR+"')");
			
			ResultSet rs = stmt.executeQuery("SELECT * FROM CaseNote WHERE caseNoteId = LAST_INSERT_ID()");
			rs.first();
			return new CaseNote(rs).getCaseNoteId();
			
			
		} catch (final SQLException ex) {
			ex.printStackTrace();
			System.err.println("Case Note not added");
			return -1;
		}
	}
	
	//TODO Implement all missing data access methods
}
