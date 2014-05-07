package ch.ethz.inf.dbproject.sqlRevisited.test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import ch.ethz.inf.dbproject.sqlRevisited.SQLLexer;
import ch.ethz.inf.dbproject.sqlRevisited.SQLParseException;
import ch.ethz.inf.dbproject.sqlRevisited.SQLParser;
import ch.ethz.inf.dbproject.sqlRevisited.SQLToken;

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
	
	@Test
	public void testInserts() {
		testSucceedsParse(testInsertSucceeds);
		testFailsParse(testInsertFails);
	}
	
	private void testSucceedsParse(String[] tests) {
		SQLLexer lex = new SQLLexer();
		SQLParser parser = new SQLParser();
		ArrayList<SQLToken> tokens;
		int parsedUntil;
		
		for (String query : tests) {
			tokens = lex.tokenize(query);
			System.out.println(tokens);
			try {
				parsedUntil = parser.parse(tokens);
				assertTrue(parsedUntil == tokens.size());
			} catch (SQLParseException e) {
				
				e.printStackTrace();
				fail("parse failed unexpectedly for query "+ query);
				
			}
		}
	}
	
	private void testFailsParse(String[] tests) {
		SQLLexer lex = new SQLLexer();
		SQLParser parser = new SQLParser();
		ArrayList<SQLToken> tokens;
		int parsedUntil;
		
		for (String query : tests) {
			tokens = lex.tokenize(query);
			System.out.println(tokens);
			try {
				parsedUntil = parser.parse(tokens);
				
				fail("parse succeeded unexpectedly for query " + query);
			} catch (SQLParseException e) {
				
			}
		}
	}
	
	@Test
	public void testUpdates() {
		SQLLexer lex = new SQLLexer();
		SQLParser parser = new SQLParser();
		
		ArrayList<SQLToken> tokens = lex.tokenize(updateCaseIsOpenQuery);
		//int parsedUntil = parser.parse(tokens);
		//assertTrue(parsedUntil == tokens.size());
	}

}
