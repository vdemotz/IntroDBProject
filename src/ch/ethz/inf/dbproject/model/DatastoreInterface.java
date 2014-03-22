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
public final class DatastoreInterface {
/*
	//FIXME This is a temporary list of cases that will be displayed until all the methods have been properly implemented
	private final static Case[] staticCases = new Case[] { 
			new Case(0, "Noise pollution..", "1287.9%", 10000), 
			new Case(1, "Highway overspeed...", "54.7%", 250000),
			new Case(2, "Money Laundring...", "1.2%", 1000000),
			new Case(3, "Corruption...", "0.0%", 1000000000),
		};
	private final static List<Case> staticCaseList = new ArrayList<Case>();
	static {
		for (int i = 0; i < staticCases.length; i++) {
			staticCaseList.add(staticCases[i]);
		}
	}*/
	
	@SuppressWarnings("unused")
	private Connection sqlConnection;

	public DatastoreInterface() {
		//TODO Uncomment the following line once MySQL is running --Connection to server enabled
		this.sqlConnection = MySQLConnection.getInstance().getConnection();
	}
	
	public final Case getCaseById(final int id) {
try {
			
			final Statement stmt = this.sqlConnection.createStatement();
			final ResultSet rs = stmt.executeQuery("SELECT * FROM CaseDetail WHERE caseID = "+id);
			rs.first();
			
			
			//rs.close();
		//	stmt.close();

			return new Case(rs);
			
		} catch (final SQLException ex) {
			ex.printStackTrace();
			return null;			
		}

		
		
	}
	
	public final List<Case> getAllCases() {

		/**
		 * TODO this method should return all the cases in the database
		 */
		//Code example for DB access
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
		// If you chose to use PreparedStatements instead of statements, you should prepare them in the constructor of DatastoreInterface.
		
		// For the time being, we return some bogus projects
		//return staticCaseList;
	}
	
	public final List<Case> getProjectsByCategory(String c) {
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
			System.err.println("Null pointer Exception, dummy guy!");
			ex.printStackTrace();
			return null;			
		}
	}
	
	public final User getUserByUsernameAndPassword(String n, String p){
				//check in DB if the username 'n' with password 'p' is in it.
		try {
			User usr;
			final Statement stmt = this.sqlConnection.createStatement();
				//connection to database
			final ResultSet rs = stmt.executeQuery("SELECT * FROM User WHERE username = '"+n+"' and password = '"+p+"'");
					//sql request in the table 'User'
			rs.first(); //necessary, because pointer points before first row at the beginning
			usr = new User(rs);		
			rs.close();	
			stmt.close();	//close ResultSet and Statement
			return usr;
			
		} catch (final SQLException ex) {
				//if the user isn't in DB, return null
			//System.err.println("User not in DB");
			//same as in UserServlet. We have to find a way to write properly that the user not in Db (wrong password for ex.)
			return null;			
		}
	}
	//TODO Implement all missing data access methods


}
