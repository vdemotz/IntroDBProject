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
		if (tokenStream.getToken() != null) {//If not the whole stream was consumed, an error occurred
			throw new SQLParseException(tokenStream.getPosition());
		} else {
			return true;
		}
	}
	
	////
	//IMPLEMENTATION
	//Top down recursive parser (scans left to right)
	//Based directly on the grammar specified in SQLGrammar.txt
	//Since the grammar is LL(1) no backtracking is needed
	////
	
	private SQLAbstractSyntaxTree statement(SQLTokenStream tokens) throws SQLParseException {
		
		ASTNode root = new ASTNode(tokens.getToken());
		SQLAbstractSyntaxTree ast = new SQLAbstractSyntaxTree(root);
		
		if (SQLToken.SQLTokenClass.INSERTINTO == tokens.getTokenClass()) {
			tokens.advance();
			insertStatement(tokens, root);
		} else if (SQLToken.SQLTokenClass.SELECT == tokens.getTokenClass()) {
			selectStatement(tokens);
		} else if (SQLToken.SQLTokenClass.UPDATE == tokens.getTokenClass()) {
			tokens.advance();
			updateStatement(tokens, root);
		} else if (SQLToken.SQLTokenClass.DELETE == tokens.getTokenClass()) {
			tokens.advance();
			deleteStatement(tokens, root);
		} else {
			throw new SQLParseException(tokens.getPosition());
		}
		return ast;
	}
	
	
	
	////
	//INSERT
	////

	private void insertStatement(SQLTokenStream tokens, ASTNode root) throws SQLParseException {
		if (SQLToken.SQLTokenClass.UID == tokens.getTokenClass()) {
			root.addChildren(new ASTNode(tokens.getToken()));
			tokens.advance();
			insertBody(tokens, root);
		} else {
			throw new SQLParseException(SQLToken.SQLTokenClass.UID, tokens.getPosition());
		}
		
	}
	
	private void insertBody(SQLTokenStream tokens, ASTNode root) throws SQLParseException {
		
		root.addChildren(optionalParenthesisedListOfUIds(tokens));
		
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.VALUES) {
			tokens.advance();
			root.addChildren(parenthesisedListOfVariables(tokens));
		} else {
			throw new SQLParseException(SQLToken.SQLTokenClass.VALUES, tokens.getPosition());
		}
	}
	
	private ASTNode optionalParenthesisedListOfUIds(SQLTokenStream tokens) throws SQLParseException {
		ASTNode root = null;
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.OPENPAREN) {
			tokens.advance();
			root = listOfUIds(tokens);
			if (tokens.getTokenClass() == SQLToken.SQLTokenClass.CLOSEPAREN) {
				tokens.advance();
			} else {
				throw new SQLParseException(SQLToken.SQLTokenClass.CLOSEPAREN, tokens.getPosition());
			}
		}
		return root;
		
	}
	
	private ASTNode parenthesisedListOfVariables(SQLTokenStream tokens) throws SQLParseException {
		ASTNode root = null;
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.OPENPAREN) {
			tokens.advance();
			root = listOfValues(tokens);
			if (tokens.getTokenClass() == SQLToken.SQLTokenClass.CLOSEPAREN) {
				tokens.advance();
			} else {
				throw new SQLParseException(SQLToken.SQLTokenClass.CLOSEPAREN, tokens.getPosition());
			}
		} else {
			throw new SQLParseException(SQLToken.SQLTokenClass.OPENPAREN, tokens.getPosition());
		}
		return root;
	}
	
	private ASTNode listOfUIds(SQLTokenStream tokens) throws SQLParseException {
		ASTNode root = null;
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.UID) {
			root = new ASTNode(tokens.getToken());
			tokens.advance();
			root.addChildren(optionalConjunctListOfUIds(tokens));
		} else {
			throw new SQLParseException(SQLToken.SQLTokenClass.UID, tokens.getPosition());
		}
		return root;
	}
	
	private ASTNode optionalConjunctListOfUIds(SQLTokenStream tokens) throws SQLParseException {
		ASTNode root = null;
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.COMMA) {
			tokens.advance();
			root = listOfUIds(tokens);
		}
		return root;
	}
	
	private ASTNode value(SQLTokenStream tokens) throws SQLParseException {
		ASTNode ast = null;
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.ARGUMENT ||
			tokens.getTokenClass() == SQLToken.SQLTokenClass.LITERAL ||
		    tokens.getTokenClass() == SQLToken.SQLTokenClass.BOOL) {
			ast = new ASTNode(tokens.getToken());
			tokens.advance();
		} else {
			throw new SQLParseException(tokens.getPosition());
		}
		return ast;
	}
	
	private ASTNode listOfValues(SQLTokenStream tokens) throws SQLParseException {
		ASTNode root = null;
		root = value(tokens);
		root.addChildren(optionalConjunctListOfValues(tokens));
		return root;
	}
	
	private ASTNode optionalConjunctListOfValues(SQLTokenStream tokens) throws SQLParseException {
		ASTNode root = null;
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.COMMA) {
			tokens.advance();
			root = listOfValues(tokens);
		}
		return root;
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
	
	private ASTNode optionalWhereClause(SQLTokenStream tokens) throws SQLParseException {
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.WHERE) {
			return predicate(tokens);
		} 
		return null;
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
	
	private ASTNode predicate(SQLTokenStream tokens) throws SQLParseException {
		ASTNode root = new ASTNode(tokens.getToken());
		tokens.advance();
		root.addChildren(comparable(tokens));
		root.addChildren(compareOperator(tokens));
		root.addChildren(comparable(tokens));
		root.addChildren(optionalConjunctPredicate(tokens));
		return root;
	}
	
	private ASTNode compareOperator(SQLTokenStream tokens) throws SQLParseException {
		ASTNode root;
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.EQUAL ||
			tokens.getTokenClass() == SQLToken.SQLTokenClass.COMPARATOR) {
			root = new ASTNode(tokens.getToken());
			tokens.advance();
		} else {
			throw new SQLParseException(tokens.getPosition());
		}
		return root;
	}
	
	private ASTNode comparable(SQLTokenStream tokens) throws SQLParseException {
		ASTNode root;
		SQLToken.SQLTokenClass token = tokens.getTokenClass();
		if (token == SQLToken.SQLTokenClass.QID ||
			token == SQLToken.SQLTokenClass.UID ||
			token == SQLToken.SQLTokenClass.ARGUMENT ||
			token == SQLToken.SQLTokenClass.LITERAL ||
		    token == SQLToken.SQLTokenClass.BOOL) {
			root = new ASTNode(tokens.getToken());
			tokens.advance();
		} else {
			throw new SQLParseException(tokens.getPosition());
		}
		return root;
	}
	
	private ASTNode optionalConjunctPredicate(SQLTokenStream tokens) throws SQLParseException  {
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.AND) {
			return predicate(tokens);
		} else {
			return null;
		}
	}
	
	////
	//UPDATE
	////
	
	private void updateStatement(SQLTokenStream tokens, ASTNode root) throws SQLParseException {
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.UID) {
			root.addChildren(new ASTNode(tokens.getToken()));
			tokens.advance();
			if (tokens.getTokenClass() == SQLToken.SQLTokenClass.SET) {
				root.addChildren(assignmentList(tokens));
				root.addChildren(optionalWhereClause(tokens));
			} else {
				throw new SQLParseException(SQLToken.SQLTokenClass.SET, tokens.getPosition());
			}
		} else {
			throw new SQLParseException(SQLToken.SQLTokenClass.UID, tokens.getPosition());
		}
		
	}

	private ASTNode assignmentList(SQLTokenStream tokens) throws SQLParseException {
		ASTNode root = new ASTNode(tokens.getToken());
		tokens.advance();
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.UID) {
			root.addChildren(new ASTNode(tokens.getToken()));
			tokens.advance();
			if (tokens.getTokenClass() == SQLToken.SQLTokenClass.EQUAL) {
				root.addChildren(new ASTNode(tokens.getToken()));
				tokens.advance();
				root.addChildren(value(tokens));
				root.addChildren(optionalConjunctAssignmentList(tokens));
			} else {
				throw new SQLParseException(SQLToken.SQLTokenClass.EQUAL, tokens.getPosition());
			}
		} else {
			throw new SQLParseException(SQLToken.SQLTokenClass.UID, tokens.getPosition());
		}
		return root;
	}
	
	private ASTNode optionalConjunctAssignmentList(SQLTokenStream tokens) throws SQLParseException {
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.COMMA) {
			tokens.advance();
			return assignmentList(tokens);
		}
		return null;
		
	}
	
	////
	//DELETE
	////
	
	private void deleteStatement(SQLTokenStream tokens, ASTNode root) throws SQLParseException {
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.UID) {
			root.addChildren(new ASTNode(tokens.getToken()));
			tokens.advance();
			if (tokens.getTokenClass() == SQLToken.SQLTokenClass.WHERE) {
				root.addChildren(predicate(tokens));
			} else {
				throw new SQLParseException(SQLToken.SQLTokenClass.WHERE, tokens.getPosition());
			}
		} else {
			throw new SQLParseException(SQLToken.SQLTokenClass.UID, tokens.getPosition());
		}
	}
}
