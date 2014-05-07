package ch.ethz.inf.dbproject.sqlRevisited;

import java.util.ArrayList;
import ch.ethz.inf.dbproject.sqlRevisited.*;

public class SQLParser {

	////
	//PUBLIC
	////
	
	public int parse(ArrayList<SQLToken> tokenStream) {
		System.out.println(tokenStream);
		return statement(tokenStream);
	}
	
	////
	//IMPLEMENTATION
	//Top down recursive parser
	////
	
	private int statement(ArrayList<SQLToken> tokens) {
		int position = 0;
		if (SQLToken.SQLTokenClass.INSERTINTO == getTokenClass(tokens, position)) {
				System.out.println(tokens.get(position));
				position += 1;
				position = insertStatement(tokens, position);
				
		}
		return position;
	}

	private int insertStatement(ArrayList<SQLToken> tokens, int position) {
		if (SQLToken.SQLTokenClass.UID == getTokenClass(tokens, position)) {
			System.out.println(tokens.get(position));
			position += 1;
			position = insertBody(tokens, position);
		}
		return position;
	}
	
	private int insertBody(ArrayList<SQLToken> tokens, int position) {
		
		if (getTokenClass(tokens, position) == SQLToken.SQLTokenClass.VALUES) {
			System.out.println(tokens.get(position));
			position += 1;
			position = parenthesisedListOfVariables(tokens, position);
		} else if (getTokenClass(tokens, position) == SQLToken.SQLTokenClass.OPENPAREN) {
			System.out.println(tokens.get(position));
			position += 1;
			position = listOfUIds(tokens, position);
			if (getTokenClass(tokens, position) == SQLToken.SQLTokenClass.CLOSEPAREN) {
				System.out.println(tokens.get(position));
				position += 1;
				if (getTokenClass(tokens, position) == SQLToken.SQLTokenClass.VALUES) {
					System.out.println(tokens.get(position));
					position += 1;
					position = parenthesisedListOfVariables(tokens, position);
				}
			}
		}
		
		return position;
	}
	
	private int parenthesisedListOfVariables(ArrayList<SQLToken> tokens, int position) {
		if (getTokenClass(tokens, position) == SQLToken.SQLTokenClass.OPENPAREN) {
			System.out.println(tokens.get(position));
			position += 1;
			position = listOfValues(tokens, position);
			if (getTokenClass(tokens, position) == SQLToken.SQLTokenClass.CLOSEPAREN) {
				System.out.println(tokens.get(position));
				position += 1;
			}
		}
		return position;
	}
	
	private int listOfUIds(ArrayList<SQLToken> tokens, int position) {
		if (getTokenClass(tokens, position) == SQLToken.SQLTokenClass.UID) {
			System.out.println(tokens.get(position));
			position += 1;
			position = optionalConjunctListOfUIds(tokens, position);
		}
		return position;
	}
	
	private int optionalConjunctListOfUIds(ArrayList<SQLToken> tokens, int position) {
		if (getTokenClass(tokens, position) == SQLToken.SQLTokenClass.COMMA) {
			System.out.println(tokens.get(position));
			position += 1;
			position = listOfUIds(tokens, position);
		}
		return position;
	}
	
	private int listOfValues(ArrayList<SQLToken> tokens, int position) {
		if (getTokenClass(tokens, position) == SQLToken.SQLTokenClass.ARGUMENT ||
		    getTokenClass(tokens, position) == SQLToken.SQLTokenClass.LITERAL ||
		    getTokenClass(tokens, position) == SQLToken.SQLTokenClass.BOOL) {
			System.out.println(tokens.get(position));
			position += 1;
			position = optionalConjunctListOfValues(tokens, position);
		}
		return position;
	}
	
	private int optionalConjunctListOfValues(ArrayList<SQLToken> tokens, int position) {
		if (getTokenClass(tokens, position) == SQLToken.SQLTokenClass.COMMA) {
			System.out.println(tokens.get(position));
			position += 1;
			position = listOfValues(tokens, position);
		}
		return position;
	}
	
	private SQLToken.SQLTokenClass getTokenClass(ArrayList<SQLToken> array, int index) {
		if (index >= array.size()) {
			return null;
		}
		return array.get(index).tokenClass;
	}
}
