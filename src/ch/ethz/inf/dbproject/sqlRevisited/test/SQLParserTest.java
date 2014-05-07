package ch.ethz.inf.dbproject.sqlRevisited.test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import ch.ethz.inf.dbproject.sqlRevisited.SQLLexer;
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
	
	@Test
	public void test() {
		SQLLexer lex = new SQLLexer();
		SQLParser parser = new SQLParser();
		
		//Should parse
		ArrayList<SQLToken> tokens = lex.tokenize(insertIntoCaseNoteQuery);
		int parsedUntil = parser.parse(tokens);
		assertTrue(parsedUntil == tokens.size());
		
		tokens = lex.tokenize(addPersonString);
		parsedUntil = parser.parse(tokens);
		assertTrue(parsedUntil == tokens.size());
		
		//Shouldn't parse
		tokens = lex.tokenize(insertQueryE1);
		parsedUntil = parser.parse(tokens);
		assertFalse(parsedUntil == tokens.size());
		
		tokens = lex.tokenize(insertQueryE2);
		parsedUntil = parser.parse(tokens);
		assertFalse(parsedUntil == tokens.size());
		
		tokens = lex.tokenize(insertQueryE3);
		parsedUntil = parser.parse(tokens);
		assertFalse(parsedUntil == tokens.size());
		
		tokens = lex.tokenize(insertQueryE4);
		parsedUntil = parser.parse(tokens);
		assertFalse(parsedUntil == tokens.size());
	}

}
