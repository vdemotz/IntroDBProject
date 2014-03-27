package ch.ethz.inf.dbproject.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.ethz.inf.dbproject.model.CaseDetail;
import ch.ethz.inf.dbproject.model.Datesql;
import ch.ethz.inf.dbproject.model.Person;
import ch.ethz.inf.dbproject.model.PersonNote;
import ch.ethz.inf.dbproject.model.User;

public class PersonDatastore implements PersonDatastoreInterface {

	private Connection sqlConnection;
	
	////
	// String for Prepared Statement
	////
	private String getCasesForWhichPersonIsConvicted = "select cd.* from CaseDetail cd, Convicted c "+
			"where c.personId = ? and cd.caseId = c.caseId";
	private String getCasesForWhichPersonIsSuspected = "select cd.* from CaseDetail cd, Suspected s "+
			"where s.personId = ? and cd.caseId = s.caseId";
	private String getPersonNotesForPerson = "select * from PersonNote where personId = ?";
	private String getPersonsForName = "select * from Person where firstName like ? or lastName like ?";
	private String getPersonsForConvictionType = "select p.* from Convicted convicted, Person p, ConvictionType convictionType "+
			"where convicted.personId = p.personId and convicted.convictionId = convictionType.convictionId and "+
			"convictionType.categoryName = ?";
	private String getPersonsForConvictionDate = "select person.* from Conviction conviction, Convicted convicted, Person person "+
			"where conviction.startDate = ? and conviction.convictionId = convicted.convictionId and "+
			"convicted.personId = person.personId";
	private String getPersonForId = "select * from Person where personId =?";

	public PersonDatastore() {
		this.sqlConnection = MySQLConnection.getInstance().getConnection();
	}
	
	@Override
	public List<CaseDetail> getCasesForWhichPersonIsConvicted(int personId) {
		try{
			PreparedStatement sqlRequest = sqlConnection.prepareStatement(this.getCasesForWhichPersonIsConvicted);
			sqlRequest.setInt(1, personId);
			ResultSet rs = sqlRequest.executeQuery();
			List<CaseDetail> cd = new ArrayList<CaseDetail>();
			while (rs.next()){
				cd.add(new CaseDetail(rs));
			}
			return cd;
		} catch (final SQLException ex){
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public List<CaseDetail> getCasesForWhichPersonIsSuspected(int personId) {
		try{
			PreparedStatement sqlRequest = sqlConnection.prepareStatement(this.getCasesForWhichPersonIsSuspected);
			sqlRequest.setInt(1, personId);
			ResultSet rs = sqlRequest.executeQuery();
			List<CaseDetail> cd = new ArrayList<CaseDetail>();
			while (rs.next()){
				cd.add(new CaseDetail(rs));
			}
			return cd;
		} catch (final SQLException ex){
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public List<PersonNote> getPersonNotesForPerson(int personId) {
		try{
			PreparedStatement sqlRequest = sqlConnection.prepareStatement(this.getPersonNotesForPerson);
			sqlRequest.setInt(1, personId);
			ResultSet rs = sqlRequest.executeQuery();
			List<PersonNote> pn = new ArrayList<PersonNote>();
			while (rs.next()){
				pn.add(new PersonNote(rs));
			}
			return pn;
		} catch (final SQLException ex){
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public List<Person> getPersonsForName(String firstName, String lastName) {
		try{
			PreparedStatement sqlRequest = sqlConnection.prepareStatement(this.getPersonsForName);
			sqlRequest.setString(1, firstName);
			sqlRequest.setString(2, lastName);
			ResultSet rs = sqlRequest.executeQuery();
			List<Person> p = new ArrayList<Person>();
			while (rs.next()){
				p.add(new Person(rs));
			}
			return p;
		} catch (final SQLException ex){
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public List<Person> getPersonsForConvictionType(String categoryName) {
		try{
			PreparedStatement sqlRequest = sqlConnection.prepareStatement(this.getPersonsForConvictionType);
			sqlRequest.setString(1, categoryName);
			ResultSet rs = sqlRequest.executeQuery();
			List<Person> p = new ArrayList<Person>();
			while (rs.next()){
				p.add(new Person(rs));
			}
			return p;
		} catch (final SQLException ex){
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public List<Person> getPersonsForConvictionDate(Datesql startDate) {
		try{
			PreparedStatement sqlRequest = sqlConnection.prepareStatement(this.getPersonsForConvictionDate);
			sqlRequest.setString(1, startDate.getDatesql());
			ResultSet rs = sqlRequest.executeQuery();
			List<Person> p = new ArrayList<Person>();
			while (rs.next()){
				p.add(new Person(rs));
			}
			return p;
		} catch (final SQLException ex){
			ex.printStackTrace();
			return null;
		}
	}
	
	@Override
	public Person getPersonForId(int personId) {
		try{
			PreparedStatement sqlRequest = sqlConnection.prepareStatement(this.getPersonForId);
			sqlRequest.setInt(1, personId);
			ResultSet rs = sqlRequest.executeQuery();
			if (!rs.first()){
				sqlRequest.close();
				rs.close();
				return null;
			}
			Person p = new Person(rs);
			rs.close();
			sqlRequest.close();
			return p;
		} catch (final SQLException ex){
			ex.printStackTrace();
			return null;
		}
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
	public PersonNote addPersonNote(int personId, String text, String authorUsername) {
		// TODO Auto-generated method stub
		return null;
	}


}
