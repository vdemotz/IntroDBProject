package ch.ethz.inf.dbproject.sqlRevisited.test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import ch.ethz.inf.dbproject.sqlRevisited.SQLLexer;
import ch.ethz.inf.dbproject.sqlRevisited.SQLParseException;
import ch.ethz.inf.dbproject.sqlRevisited.SQLParser;
import ch.ethz.inf.dbproject.sqlRevisited.SQLToken;
import ch.ethz.inf.dbproject.sqlRevisited.SQLTokenStream;

public class SQLParserTest {

	private static final String insertIntoCaseNoteQuery = "insert into CaseNote " +
			"values (4, " +//caseId
			"?, " +//caseNoteId
			"'its alive!', " +//text
			"?, " +//date
			"?)";//authorUsername
	
	private static final String addPersonString = "insert into Person(personId, firstName, lastName, birthdate) values(?, ?, ?, ?)";
	
	private static final String insertIntoConvictionQuery = "insert into Conviction values (?, ?, ?, ?, ?)";
	
	//missing close parent
	private static final String insertQueryE1 = "insert into CaseNote(caseId, authorUsername " +
			"values (4, " +//caseId
			"?)";//authorUsername
	
	//missing open paren
	private static final String insertQueryE2 = "insert into CaseNote caseId, authorUsername) " +
			"values (?, " +//caseId
			"?)";//authorUsername
	
	//missing comma
	private static final String insertQueryE3 = "insert into CaseNote (caseId, authorUsername) " +
			"values (? " +//caseId
			"?)";//authorUsername
	
	private static final String insertQueryE4 = "insert into CaseNote " +
			"values (notaliteral)";
	
	private static final String insertQueryE5 = "insert into CaseNote() values(?)";
	
	private static final String updateCaseIsOpenQuery = "update CaseDetail set isOpen = ? where caseId = ?";
	
	private String[] testInsertSucceeds = {insertIntoCaseNoteQuery, addPersonString, insertIntoConvictionQuery};
	
	private String[] testInsertFails = {insertQueryE1, insertQueryE2, insertQueryE3, insertQueryE4, insertQueryE5};
	
	
	
	private final String selectQueryE1 = "select authorUsername from , , ";
	
	private final String selectQueryE2 = "select authorUsername from CaseDetail distinct asc";
	
	private static final String suspectsForCaseQuery = "select person.* " +
			  "from Person person, Suspected suspected " +
            "where suspected.caseId = ? and suspected.personId = person.personId";
	
	private static final String getPersonsForConvictionTypeString = "select person.* " +
			   "from Conviction conviction, Person person, CategoryForCase categoryForCase "+
			   "where conviction.personId = person.personId "+
			   "and conviction.caseId = categoryForCase.caseId " +
			   "and categoryForCase.categoryName = ? " +
			   "order by lastName, firstName";
	
	private static final String categoriesForCaseQuery = "select distinct Category.* " +
			"from CaseDetail caseDetail, CategoryForCase categoryForCase, Category category " +
		    "where caseDetail.caseId = ? and categoryForCase.caseId = caseDetail.caseId and categoryForCase.categoryName = category.Name";
	
	private static final String getAllPersonsString = "select * from Person order by lastName desc, firstName desc";
	
	private String[] testSelectFails = {selectQueryE1, selectQueryE2, "select order by name", "select A B from C", "select A, B from C, (select * from C)"};
	
	private String[] testSelectSucceeds = {suspectsForCaseQuery, getPersonsForConvictionTypeString, "select A, B from C, (select * from C) as B", categoriesForCaseQuery, getAllPersonsString};
	
	

	
	private String[] testUpdateFails = {"update A set A = B, ", "update A set B"};
	
	private String[] testUpdateSucceeds = {updateCaseIsOpenQuery, "update A set a=3, b=5, c=?, h=true"};
	
	
	
	
	private static final String deleteSuspectFromCaseQuery = "delete from Suspected where caseId=? and personId=?";
	
	private String[] testDeleteFails = {"delete", "delete A", "delete A where"};

	private String[] testDeleteSucceeds = {deleteSuspectFromCaseQuery};
	
	
	@Test
	public void testInserts() {
		testSucceedsParse(testInsertSucceeds);
		testFailsParse(testInsertFails);
	}
	
	@Test
	public void testSelect() {
		testFailsParse(testSelectFails);
		testSucceedsParse(testSelectSucceeds);
	}
	
	@Test
	public void testUpdates() {
		testFailsParse(testUpdateFails);
		testSucceedsParse(testUpdateSucceeds);
	}
	
	@Test
	public void testDeletes() {
		testFailsParse(testDeleteFails);
		testSucceedsParse(testDeleteSucceeds);
	}
	
	private void testSucceedsParse(String[] tests) {
		SQLLexer lex = new SQLLexer();
		SQLParser parser = new SQLParser();
		SQLTokenStream tokens;
		int parsedUntil;
		
		for (String query : tests) {
			tokens = new SQLTokenStream(lex.tokenize(query));
			try {
				assertTrue(parser.parse(tokens));
				//System.out.println(tokens);
				assertTrue(parser.parse(tokens));
				System.out.println(tokens);
			} catch (SQLParseException e) {
				System.out.println(tokens);
				e.printStackTrace();
				fail("parse failed unexpectedly for query "+ query);
				
			}
		}
	}
	
	private void testFailsParse(String[] tests) {
		SQLLexer lex = new SQLLexer();
		SQLParser parser = new SQLParser();
		SQLTokenStream tokens;
		int parsedUntil;
		
		for (String query : tests) {
			tokens = new SQLTokenStream(lex.tokenize(query));
			try {
				parser.parse(tokens);
				System.out.println(tokens);
				fail("parse succeeded unexpectedly for query " + query);
			} catch (SQLParseException e) {
				System.out.println(tokens);
			}
		}
	}

}
