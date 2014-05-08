package ch.ethz.inf.dbproject.sqlRevisited;

import java.util.ArrayList;
import ch.ethz.inf.dbproject.sqlRevisited.*;

public class SQLParser {

	////
	//PUBLIC
	////
	
	/**
	 * Rewinds the token stream and tries to parse it
	 * @param tokenStream
	 * @return true if the stream parsed, false otherwise
	 * @throws SQLParseException if a parsing error occurs
	 */
	public boolean parse(SQLTokenStream tokenStream) throws SQLParseException {
		tokenStream.rewind();
		statement(tokenStream);
		return tokenStream.getToken() == null;
	}
	
	////
	//IMPLEMENTATION
	//Top down recursive parser
	////
	
	private void statement(SQLTokenStream tokens) throws SQLParseException {

		if (SQLToken.SQLTokenClass.INSERTINTO == tokens.getTokenClass()) {
			tokens.advance();
			insertStatement(tokens);
		} else {
			throw new SQLParseException(SQLToken.SQLTokenClass.INSERTINTO, tokens.getPosition());
		}
	}
	
	////
	//INSERT
	////

	private void insertStatement(SQLTokenStream tokens) throws SQLParseException {
		if (SQLToken.SQLTokenClass.UID == tokens.getTokenClass()) {
			tokens.advance();
			insertBody(tokens);
		} else {
			throw new SQLParseException(SQLToken.SQLTokenClass.UID, tokens.getPosition());
		}
		
	}
	
	private void insertBody(SQLTokenStream tokens) throws SQLParseException {
		
		optionalParenthesisedListOfUIds(tokens);
		
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.VALUES) {
			tokens.advance();
			parenthesisedListOfVariables(tokens);
		} else {
			throw new SQLParseException(SQLToken.SQLTokenClass.VALUES, tokens.getPosition());
		}
		
	}
	
	private void optionalParenthesisedListOfUIds(SQLTokenStream tokens) throws SQLParseException {
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.OPENPAREN) {
			tokens.advance();
			listOfUIds(tokens);
			if (tokens.getTokenClass() == SQLToken.SQLTokenClass.CLOSEPAREN) {
				tokens.advance();
			} else {
				throw new SQLParseException(SQLToken.SQLTokenClass.CLOSEPAREN, tokens.getPosition());
			}
		}
		
	}
	
	private void parenthesisedListOfVariables(SQLTokenStream tokens) throws SQLParseException {
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.OPENPAREN) {
			tokens.advance();
			listOfValues(tokens);
			if (tokens.getTokenClass() == SQLToken.SQLTokenClass.CLOSEPAREN) {
				tokens.advance();
			} else {
				throw new SQLParseException(SQLToken.SQLTokenClass.CLOSEPAREN, tokens.getPosition());
			}
		} else {
			throw new SQLParseException(SQLToken.SQLTokenClass.OPENPAREN, tokens.getPosition());
		}
		
	}
	
	private void listOfUIds(SQLTokenStream tokens) throws SQLParseException {
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.UID) {
			tokens.advance();
			optionalConjunctListOfUIds(tokens);
		} else {
			throw new SQLParseException(SQLToken.SQLTokenClass.UID, tokens.getPosition());
		}
		
	}
	
	private void optionalConjunctListOfUIds(SQLTokenStream tokens) throws SQLParseException {
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.COMMA) {
			tokens.advance();
			listOfUIds(tokens);
		}
		
	}
	
	private void listOfValues(SQLTokenStream tokens) throws SQLParseException {
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.ARGUMENT ||
		    tokens.getTokenClass() == SQLToken.SQLTokenClass.LITERAL ||
		    tokens.getTokenClass() == SQLToken.SQLTokenClass.BOOL) {
			tokens.advance();
			optionalConjunctListOfValues(tokens);
		} else {
			throw new SQLParseException(tokens.getPosition());
		}
		
	}
	
	private void optionalConjunctListOfValues(SQLTokenStream tokens) throws SQLParseException {
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.COMMA) {
			tokens.advance();
			listOfValues(tokens);
		}
		
	}
	
	////
	//SELECT
	////
	
	////
	//PREDICATES
	////
	
	private void predicate(SQLTokenStream tokens) throws SQLParseException {
		comparable(tokens);
		compareOperator(tokens);
		comparable(tokens);
		optionalConjunctPredicate(tokens);
	}
	
	private void compareOperator(SQLTokenStream tokens) throws SQLParseException {
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.EQUAL ||
			tokens.getTokenClass() == SQLToken.SQLTokenClass.COMPARATOR) {
			tokens.advance();
		} else {
			throw new SQLParseException(tokens.getPosition());
		}
		
	}
	
	private void comparable(SQLTokenStream tokens) throws SQLParseException {
		
		SQLToken.SQLTokenClass token = tokens.getTokenClass();
		if (token == SQLToken.SQLTokenClass.QID ||
			token == SQLToken.SQLTokenClass.UID ||
			token == SQLToken.SQLTokenClass.ARGUMENT ||
			token == SQLToken.SQLTokenClass.LITERAL ||
		    token == SQLToken.SQLTokenClass.BOOL) {
			tokens.advance();
		} else {
			throw new SQLParseException(tokens.getPosition());
		}
		
	}
	
	private void optionalConjunctPredicate(SQLTokenStream tokens) throws SQLParseException  {
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.AND) {
			tokens.advance();
			predicate(tokens);
		}
		
	}
	
	
	
}
