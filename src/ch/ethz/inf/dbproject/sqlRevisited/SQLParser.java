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
		if (tokenStream.getToken() != null) {//If not the whole stream was consumed, an error occured
			throw new SQLParseException(tokenStream.getPosition());
		} else {
			return true;
		}
	}
	
	////
	//IMPLEMENTATION
	//Top down recursive parser
	////
	
	private void statement(SQLTokenStream tokens) throws SQLParseException {

		if (SQLToken.SQLTokenClass.INSERTINTO == tokens.getTokenClass()) {
			tokens.advance();
			insertStatement(tokens);
		} else if (SQLToken.SQLTokenClass.SELECT == tokens.getTokenClass()){
			selectStatement(tokens);
		} else {
			throw new SQLParseException(tokens.getPosition());
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
	
	private void selectStatement(SQLTokenStream tokens) throws SQLParseException {
		selectStatementInner(tokens);
		optionalOrderByClause(tokens);
	}
	
	private void optionalOrderByClause(SQLTokenStream tokens) throws SQLParseException {
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.ORDERBY) {
			tokens.advance();
			concreteListOfAttributes(tokens);
			
		}
	}
	
	private void concreteAttribute(SQLTokenStream tokens) throws SQLParseException {
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.UID ||
				tokens.getTokenClass() == SQLToken.SQLTokenClass.QID) {
			tokens.advance();
		} else {
			throw new SQLParseException(tokens.getPosition());
		}
	}
	
	private void concreteListOfAttributes(SQLTokenStream tokens) throws SQLParseException {
		concreteAttribute(tokens);
		optionalOrderDirection(tokens);
		optionalConjunctConcreteListOfAttributes(tokens);
	}
	
	private void optionalConjunctConcreteListOfAttributes(SQLTokenStream tokens) throws SQLParseException {
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.COMMA) {
			tokens.advance();
			concreteListOfAttributes(tokens);
		}
	}
	
	private void optionalOrderDirection(SQLTokenStream tokens) throws SQLParseException {
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.ORDERDIRECTION) {
			tokens.advance();
		}
	}

	
	private void selectStatementInner(SQLTokenStream tokens) throws SQLParseException {
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.SELECT) {
			tokens.advance();
			optionalDistinct(tokens);
			selectBody(tokens);
		} else {
			throw new SQLParseException(tokens.getPosition());
		}
	}
	
	private void optionalDistinct(SQLTokenStream tokens) throws SQLParseException {
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.DISTINCT) {
			tokens.advance();
		}
	}
	
	private void subSelectStatement(SQLTokenStream tokens) throws SQLParseException {
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.OPENPAREN) {
			tokens.advance();
			selectStatement(tokens);
			if (tokens.getTokenClass() == SQLToken.SQLTokenClass.CLOSEPAREN) {
				tokens.advance();
			} else {
				throw new SQLParseException(tokens.getPosition());
			}
		} else {
			throw new SQLParseException(tokens.getPosition());
		}
	}
	
	
	private void selectBody(SQLTokenStream tokens) throws SQLParseException {
		selectionList(tokens);
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.FROM) {
			tokens.advance();
		} else {
			throw new SQLParseException(tokens.getPosition());
		}
		fromList(tokens);
		optionalWhereClause(tokens);
		optionalGroupClause(tokens);
	}
	
	private void selectionList(SQLTokenStream tokens) throws SQLParseException {
		selectable(tokens);
		optionalConjunctSelectionList(tokens);
	}
	
	private void optionalConjunctSelectionList(SQLTokenStream tokens) throws SQLParseException {
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.COMMA) {
			tokens.advance();
			selectionList(tokens);
		}
	}
	
	
	private void selectable(SQLTokenStream tokens) throws SQLParseException {
		if (	tokens.getTokenClass() == SQLToken.SQLTokenClass.STAR ||
				tokens.getTokenClass() == SQLToken.SQLTokenClass.QID ||
				tokens.getTokenClass() == SQLToken.SQLTokenClass.UID ||
				tokens.getTokenClass() == SQLToken.SQLTokenClass.QSTARID) {
			tokens.advance();
		} else if (tokens.getTokenClass() == SQLToken.SQLTokenClass.AGGREGATE) {
			tokens.advance();
			renamable(tokens);
		} else {
			throw new SQLParseException(tokens.getPosition());
		}
	}
	
	private void fromId(SQLTokenStream tokens) throws SQLParseException {
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.UID) {
			tokens.advance();
			renamable(tokens);
		} else if (tokens.getTokenClass() == SQLToken.SQLTokenClass.OPENPAREN) {
			subSelectStatement(tokens);
			if (tokens.getTokenClass() == SQLToken.SQLTokenClass.AS) {
				tokens.advance();
				if (tokens.getTokenClass() == SQLToken.SQLTokenClass.UID) {
					tokens.advance();
				} else { throw new SQLParseException(tokens.getPosition()); }
			} else { throw new SQLParseException(tokens.getPosition()); }
		}  else {
			throw new SQLParseException(tokens.getPosition());
		}
	}
	
	private void renamable(SQLTokenStream tokens) throws SQLParseException {
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.AS) {
			tokens.advance();
			if (tokens.getTokenClass() == SQLToken.SQLTokenClass.UID) {
				tokens.advance();
			} else {
				throw new SQLParseException(tokens.getPosition());
			}
		}
	}
	
	private void fromList(SQLTokenStream tokens) throws SQLParseException {
		fromId(tokens);
		optionalConjunctFromList(tokens);
	}
	
	private void optionalConjunctFromList(SQLTokenStream tokens) throws SQLParseException {
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.COMMA) {
			tokens.advance();
			fromList(tokens);
		} 
	}
	
	private void optionalWhereClause(SQLTokenStream tokens) throws SQLParseException {
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.WHERE) {
			tokens.advance();
			predicate(tokens);
		} 
	}
	
	private void optionalGroupClause(SQLTokenStream tokens) throws SQLParseException {
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.GROUPBY) {
			tokens.advance();
			listOfUIds(tokens);
		} 
	}
	
	
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
