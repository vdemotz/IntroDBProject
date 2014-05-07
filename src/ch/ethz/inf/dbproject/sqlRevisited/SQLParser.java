package ch.ethz.inf.dbproject.sqlRevisited;

import java.util.ArrayList;
import ch.ethz.inf.dbproject.sqlRevisited.*;

public class SQLParser {

	////
	//PUBLIC
	////
	
	public int parse(ArrayList<SQLToken> tokenStream) throws SQLParseException {
		return statement(tokenStream);
	}
	
	////
	//IMPLEMENTATION
	//Top down recursive parser
	////
	
	private int statement(ArrayList<SQLToken> tokens) throws SQLParseException {
		int position = 0;
		if (SQLToken.SQLTokenClass.INSERTINTO == getTokenClass(tokens, position)) {
			position += 1;
			position = insertStatement(tokens, position);
		} else {
			throw new SQLParseException(SQLToken.SQLTokenClass.INSERTINTO, position);
		}
		return position;
	}
	
	////
	//INSERT
	////

	private int insertStatement(ArrayList<SQLToken> tokens, int position) throws SQLParseException {
		if (SQLToken.SQLTokenClass.UID == getTokenClass(tokens, position)) {
			position += 1;
			position = insertBody(tokens, position);
		} else {
			throw new SQLParseException(SQLToken.SQLTokenClass.UID, position);
		}
		return position;
	}
	
	private int insertBody(ArrayList<SQLToken> tokens, int position) throws SQLParseException {
		
		position = optionalParenthesisedListOfUIds(tokens, position);
		
		if (getTokenClass(tokens, position) == SQLToken.SQLTokenClass.VALUES) {
			position += 1;
			position = parenthesisedListOfVariables(tokens, position);
		} else {
			throw new SQLParseException(SQLToken.SQLTokenClass.VALUES, position);
		}
		
		return position;
	}
	
	private int optionalParenthesisedListOfUIds(ArrayList<SQLToken> tokens, int position) throws SQLParseException {
		if (getTokenClass(tokens, position) == SQLToken.SQLTokenClass.OPENPAREN) {
			position += 1;
			position = listOfUIds(tokens, position);
			if (getTokenClass(tokens, position) == SQLToken.SQLTokenClass.CLOSEPAREN) {
				position += 1;
			} else {
				throw new SQLParseException(SQLToken.SQLTokenClass.CLOSEPAREN, position);
			}
		}
		return position;
	}
	
	private int parenthesisedListOfVariables(ArrayList<SQLToken> tokens, int position) throws SQLParseException {
		if (getTokenClass(tokens, position) == SQLToken.SQLTokenClass.OPENPAREN) {
			position += 1;
			position = listOfValues(tokens, position);
			if (getTokenClass(tokens, position) == SQLToken.SQLTokenClass.CLOSEPAREN) {
				position += 1;
			} else {
				throw new SQLParseException(SQLToken.SQLTokenClass.CLOSEPAREN, position);
			}
		} else {
			throw new SQLParseException(SQLToken.SQLTokenClass.OPENPAREN, position);
		}
		return position;
	}
	
	private int listOfUIds(ArrayList<SQLToken> tokens, int position) throws SQLParseException {
		if (getTokenClass(tokens, position) == SQLToken.SQLTokenClass.UID) {
			position += 1;
			position = optionalConjunctListOfUIds(tokens, position);
		} else {
			throw new SQLParseException(SQLToken.SQLTokenClass.UID, position);
		}
		return position;
	}
	
	private int optionalConjunctListOfUIds(ArrayList<SQLToken> tokens, int position) throws SQLParseException {
		if (getTokenClass(tokens, position) == SQLToken.SQLTokenClass.COMMA) {
			position += 1;
			position = listOfUIds(tokens, position);
		}
		return position;
	}
	
	private int listOfValues(ArrayList<SQLToken> tokens, int position) throws SQLParseException {
		if (getTokenClass(tokens, position) == SQLToken.SQLTokenClass.ARGUMENT ||
		    getTokenClass(tokens, position) == SQLToken.SQLTokenClass.LITERAL ||
		    getTokenClass(tokens, position) == SQLToken.SQLTokenClass.BOOL) {
			position += 1;
			position = optionalConjunctListOfValues(tokens, position);
		} else {
			throw new SQLParseException(position);
		}
		return position;
	}
	
	private int optionalConjunctListOfValues(ArrayList<SQLToken> tokens, int position) throws SQLParseException {
		if (getTokenClass(tokens, position) == SQLToken.SQLTokenClass.COMMA) {
			position += 1;
			position = listOfValues(tokens, position);
		}
		return position;
	}
	
	////
	//SELECT
	////
	
	////
	//PREDICATES
	////
	
	private int predicate(ArrayList<SQLToken> tokens, int position) throws SQLParseException {
		position = comparable(tokens, position);
		position = compareOperator(tokens, position);
		position = comparable(tokens, position);
		position = optionalConjunctPredicate(tokens, position);

		return position;
	}
	
	private int compareOperator(ArrayList<SQLToken> tokens, int position) throws SQLParseException {
		if (getTokenClass(tokens, position) == SQLToken.SQLTokenClass.EQUAL ||
			getTokenClass(tokens, position) == SQLToken.SQLTokenClass.COMPARATOR) {
			position += 1;
		} else {
			throw new SQLParseException(position);
		}
		return position;
	}
	
	private int comparable(ArrayList<SQLToken> tokens, int position) throws SQLParseException {
		
		SQLToken.SQLTokenClass token = getTokenClass(tokens, position);
		if (token == SQLToken.SQLTokenClass.QID ||
			token == SQLToken.SQLTokenClass.UID ||
			token == SQLToken.SQLTokenClass.ARGUMENT ||
			token == SQLToken.SQLTokenClass.LITERAL ||
		    token == SQLToken.SQLTokenClass.BOOL) {
			position += 1;
		} else {
			throw new SQLParseException(position);
		}
		return position;
	}
	
	private int optionalConjunctPredicate(ArrayList<SQLToken> tokens, int position) throws SQLParseException  {
		if (getTokenClass(tokens, position) == SQLToken.SQLTokenClass.AND) {
			position += 1;
			position = predicate(tokens, position);
		}
		return position;
	}
	
	////
	//HELPER
	////
	
	private SQLToken.SQLTokenClass getTokenClass(ArrayList<SQLToken> array, int index) {
		if (index >= array.size()) {
			return null;
		}
		return array.get(index).tokenClass;
	}
	
	
}
