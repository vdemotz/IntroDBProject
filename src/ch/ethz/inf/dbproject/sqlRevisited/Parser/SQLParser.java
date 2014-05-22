package ch.ethz.inf.dbproject.sqlRevisited.Parser;

import java.util.ArrayList;
import ch.ethz.inf.dbproject.sqlRevisited.*;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.SQLToken.SQLTokenClass;

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
	public SyntaxTreeDynamicNode parse(SQLTokenStream tokenStream) throws SQLParseException {
		tokenStream.rewind();
		SyntaxTreeDynamicNode ast = statement(tokenStream);
		if (tokenStream.getToken() != null) {//If not the whole stream was consumed, an error occurred
			throw new SQLParseException(tokenStream.getPosition());
		} else {
			return ast;
		}
	}
	
	////
	//IMPLEMENTATION
	//Top down recursive parser (scans left to right)
	//Based directly on the grammar specified in SQLGrammar.txt
	//Since the grammar is LL(1) no backtracking is needed
	////
	
	private SyntaxTreeDynamicNode statement(SQLTokenStream tokens) throws SQLParseException {
		
		SyntaxTreeDynamicNode droot = new SyntaxTreeDynamicNode(tokens.getToken());
		
		if (SQLToken.SQLTokenClass.INSERTINTO == tokens.getTokenClass()) {
			tokens.advance();
			insertStatement(tokens, droot);
		} else if (SQLToken.SQLTokenClass.SELECT == tokens.getTokenClass()) {
			SyntaxTreeNode root = selectStatement(tokens);
			droot.addChildren(root);
		} else if (SQLToken.SQLTokenClass.UPDATE == tokens.getTokenClass()) {
			tokens.advance();
			updateStatement(tokens, droot);
		} else if (SQLToken.SQLTokenClass.DELETE == tokens.getTokenClass()) {
			tokens.advance();
			deleteStatement(tokens, droot);
		} else {
			throw new SQLParseException(tokens.getPosition());
		}
		
		return droot;
	}
	
	
	
	////
	//INSERT
	////

	private void insertStatement(SQLTokenStream tokens, SyntaxTreeDynamicNode root) throws SQLParseException {
		if (SQLToken.SQLTokenClass.UID == tokens.getTokenClass()) {
			root.addChildren(new SyntaxTreeDynamicNode(tokens.getToken()));
			tokens.advance();
			insertBody(tokens, root);
		} else {
			throw new SQLParseException(SQLToken.SQLTokenClass.UID, tokens.getPosition());
		}
		
	}
	
	private void insertBody(SQLTokenStream tokens, SyntaxTreeDynamicNode root) throws SQLParseException {
		
		root.addChildren(optionalParenthesisedListOfUIds(tokens));
		
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.VALUES) {
			tokens.advance();
			root.addChildren(parenthesisedListOfVariables(tokens));
		} else {
			throw new SQLParseException(SQLToken.SQLTokenClass.VALUES, tokens.getPosition());
		}
	}
	
	private SyntaxTreeDynamicNode optionalParenthesisedListOfUIds(SQLTokenStream tokens) throws SQLParseException {
		SyntaxTreeDynamicNode root = null;
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
	
	private SyntaxTreeDynamicNode parenthesisedListOfVariables(SQLTokenStream tokens) throws SQLParseException {
		SyntaxTreeDynamicNode root = null;
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
	
	private SyntaxTreeDynamicNode listOfUIds(SQLTokenStream tokens) throws SQLParseException {
		SyntaxTreeDynamicNode root = null;
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.UID) {
			root = new SyntaxTreeDynamicNode(tokens.getToken());
			tokens.advance();
			root.addChildren(optionalConjunctListOfUIds(tokens));
		} else {
			throw new SQLParseException(SQLToken.SQLTokenClass.UID, tokens.getPosition());
		}
		return root;
	}
	
	private SyntaxTreeDynamicNode optionalConjunctListOfUIds(SQLTokenStream tokens) throws SQLParseException {
		SyntaxTreeDynamicNode root = null;
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.COMMA) {
			tokens.advance();
			root = listOfUIds(tokens);
		}
		return root;
	}
	
	private SyntaxTreeDynamicNode value(SQLTokenStream tokens) throws SQLParseException {
		SyntaxTreeDynamicNode ast = null;
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.ARGUMENT ||
			tokens.getTokenClass() == SQLToken.SQLTokenClass.LITERAL ||
			tokens.getTokenClass() == SQLToken.SQLTokenClass.NUMERIC ||
		    tokens.getTokenClass() == SQLToken.SQLTokenClass.BOOL) {
			ast = new SyntaxTreeDynamicNode(tokens.getToken());
			tokens.advance();
		} else {
			throw new SQLParseException(tokens.getPosition());
		}
		return ast;
	}
	
	private SyntaxTreeDynamicNode listOfValues(SQLTokenStream tokens) throws SQLParseException {
		SyntaxTreeDynamicNode root = null;
		root = value(tokens);
		root.addChildren(optionalConjunctListOfValues(tokens));
		return root;
	}
	
	private SyntaxTreeDynamicNode optionalConjunctListOfValues(SQLTokenStream tokens) throws SQLParseException {
		SyntaxTreeDynamicNode root = null;
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.COMMA) {
			tokens.advance();
			root = listOfValues(tokens);
		}
		return root;
	}
	
	////
	//SELECT
	////
	
	private SyntaxTreeNode selectStatement(SQLTokenStream tokens) throws SQLParseException {
		
		SyntaxTreeNode innerStatement = selectStatementInner(tokens);
		SyntaxTreeListNode<SyntaxTreeOrderingNode> orderStatement = optionalOrderByClause(tokens);
		
		if (orderStatement == null) {
			return innerStatement;
		} else {
			return new SyntaxTreeSortOperatorNode(innerStatement, orderStatement);
		}
	}
	
	//Ordering begin
	private SyntaxTreeListNode<SyntaxTreeOrderingNode> optionalOrderByClause(SQLTokenStream tokens) throws SQLParseException {
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.ORDERBY) {
			tokens.advance();
			return concreteListOfAttributes(tokens);
		}
		return null;
	}
	
	private SyntaxTreeIdentifierNode concreteAttribute(SQLTokenStream tokens) throws SQLParseException {
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.UID ||
			tokens.getTokenClass() == SQLToken.SQLTokenClass.QID) {
			SyntaxTreeIdentifierNode node = new SyntaxTreeIdentifierNode(tokens.getToken());
			tokens.advance();
			return node;
		} else {
			throw new SQLParseException(tokens.getPosition());
		}
	}
	
	private SyntaxTreeListNode<SyntaxTreeOrderingNode> concreteListOfAttributes(SQLTokenStream tokens) throws SQLParseException {
		SyntaxTreeIdentifierNode attribute = concreteAttribute(tokens);
		SyntaxTreeOrderingNode node = new SyntaxTreeOrderingNode(attribute.generatingToken, optionalOrderDirection(tokens));
		
		return new SyntaxTreeListNode<SyntaxTreeOrderingNode>(node, optionalConjunctConcreteListOfAttributes(tokens));
	}
	
	private SyntaxTreeListNode<SyntaxTreeOrderingNode> optionalConjunctConcreteListOfAttributes(SQLTokenStream tokens) throws SQLParseException {
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.COMMA) {
			tokens.advance();
			return concreteListOfAttributes(tokens);
		}
		return null;
	}
	
	private boolean optionalOrderDirection(SQLTokenStream tokens) throws SQLParseException {
		boolean asc = true;
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.ORDERDIRECTION) {
			if (tokens.getToken().content.equals("desc")) asc = false;
			tokens.advance();
		}
		return asc;
	}
	
	////
	//general select structure
	////
	
	private SyntaxTreeNode selectStatementInner(SQLTokenStream tokens) throws SQLParseException {
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.SELECT) {
			tokens.advance();
			boolean distinct = optionalDistinct(tokens);
			SyntaxTreeNode body = selectBody(tokens);
			if (distinct) {
				return new SyntaxTreeNodeDistinct(body);
			} else {
				return body;
			}
		} else {
			throw new SQLParseException(tokens.getPosition());
		}
	}
	
	private boolean optionalDistinct(SQLTokenStream tokens) throws SQLParseException {
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.DISTINCT) {
			tokens.advance();
			return true;
		}
		return false;
	}
	
	private SyntaxTreeNode subSelectStatement(SQLTokenStream tokens) throws SQLParseException {
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.OPENPAREN) {
			tokens.advance();
			SyntaxTreeNode result = selectStatement(tokens);
			if (tokens.getTokenClass() == SQLToken.SQLTokenClass.CLOSEPAREN) {
				tokens.advance();
				return result;
			} else {
				throw new SQLParseException(tokens.getPosition());
			}
		} else {
			throw new SQLParseException(tokens.getPosition());
		}
	}
	
	private SyntaxTreeNode selectBody(SQLTokenStream tokens) throws SQLParseException {
		//gather trees from different clauses
		SyntaxTreeListNode<SyntaxTreeNode> projectOnto = selectionClause(tokens);
		
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.FROM) {
			tokens.advance();
		} else {
			throw new SQLParseException(tokens.getPosition());
		}
		SyntaxTreeNode from = fromList(tokens);
		SyntaxTreeNode where = optionalWhereClause(tokens, from);
		SyntaxTreeListNode<SyntaxTreeIdentifierNode> groupByList = optionalGroupClause(tokens);
		
		//assemble the resulting tree
		SyntaxTreeNode result = from;
		if (where != null) {
			result = where;
		}
		if (groupByList != null) {
			result = new SyntaxTreeGroupByNode(result, groupByList);
		}
		result = resolveAggregates(projectOnto, result);
		
		result = new SyntaxTreeProjectOperatorNode(result, projectOnto);
		
		return result;
	}
	
	private String renamable(SQLTokenStream tokens) throws SQLParseException {
		String result = null;
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.AS) {
			tokens.advance();
			if (tokens.getTokenClass() == SQLToken.SQLTokenClass.UID) {
				result = tokens.getToken().content;
				tokens.advance();
			} else {
				throw new SQLParseException(tokens.getPosition());
			}
		}
		return result;
	}
	
	
	/**
	 * Utility method that, given a selectionList and a base node, returns a new tree with the aggregates from the selectionList and the base as leaf
	 */
	private SyntaxTreeNode resolveAggregates(SyntaxTreeListNode<SyntaxTreeNode> selectionList, SyntaxTreeNode base) {
		if (selectionList == null) {//base case : return the base node
			return base;
		}
		//recursively resolve the list
		if (selectionList.getNode().getClass().equals(SyntaxTreeRenameNode.class)) {
			SyntaxTreeIdentifierNode aggregateNode = (SyntaxTreeIdentifierNode) ((SyntaxTreeRenameNode)selectionList.getNode()).getChild();
			if (aggregateNode.generatingToken.tokenClass == SQLToken.SQLTokenClass.AGGREGATE) {//Case 1: aggregate node : create new aggregate with rest of resolution as child
				return new SyntaxTreeAggregateNode(aggregateNode.generatingToken, ((SyntaxTreeRenameNode)selectionList.getNode()).name, resolveAggregates(selectionList.getNext(), base));
			}
		}
		//Case 2 : not an aggregate node: continue
		return resolveAggregates(selectionList.getNext(), base);
	}
	
	////
	///Projection / aggregate choice (selection list)
	////
	
	private SyntaxTreeListNode<SyntaxTreeNode> selectionClause(SQLTokenStream tokens) throws SQLParseException {
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.STAR) {
			SyntaxTreeNode identifier = new SyntaxTreeIdentifierNode(tokens.getToken());
			tokens.advance();
			return new SyntaxTreeListNode<SyntaxTreeNode>(identifier, null);
		} else {
			return selectionList(tokens);
		}
	}
	
	private SyntaxTreeListNode<SyntaxTreeNode> selectionList(SQLTokenStream tokens) throws SQLParseException {
		SyntaxTreeNode identifier = selectable(tokens);
		return new SyntaxTreeListNode<SyntaxTreeNode>(identifier, optionalConjunctSelectionList(tokens));
	}
	
	private SyntaxTreeListNode<SyntaxTreeNode> optionalConjunctSelectionList(SQLTokenStream tokens) throws SQLParseException {
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.COMMA) {
			tokens.advance();
			return selectionList(tokens);
		}
		return null;
	}
	
	private SyntaxTreeNode selectable(SQLTokenStream tokens) throws SQLParseException {
		SyntaxTreeNode node = null;
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.QID ||
			tokens.getTokenClass() == SQLToken.SQLTokenClass.UID ||
			tokens.getTokenClass() == SQLToken.SQLTokenClass.QSTARID) {
			node = new SyntaxTreeIdentifierNode(tokens.getToken());
			tokens.advance();
		} else if (tokens.getTokenClass() == SQLToken.SQLTokenClass.AGGREGATE) {
			node = new SyntaxTreeIdentifierNode(tokens.getToken());
			tokens.advance();
			String newName = renamable(tokens);//get the name of the aggregate, if no new name was given, use the token content as name
			if (newName == null) newName = ((SyntaxTreeIdentifierNode)node).generatingToken.content;
			node = new SyntaxTreeRenameNode(node, newName);
			
		} else {
			throw new SQLParseException(tokens.getPosition());
		}
		return node;
	}
	
	////
	//Cross products (from list)
	////
	
	private SyntaxTreeNode fromId(SQLTokenStream tokens) throws SQLParseException {
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.UID) {
			SyntaxTreeBaseRelationNode relation = new SyntaxTreeBaseRelationNode(tokens.getToken().content);
			tokens.advance();
			String newName = renamable(tokens);
			if (newName != null) {
				return new SyntaxTreeRenameNode(relation, newName);
			} else {
				return relation;
			}
			
		} else if (tokens.getTokenClass() == SQLToken.SQLTokenClass.OPENPAREN) {
			SyntaxTreeNode subquery = subSelectStatement(tokens);
			String newName = renamable(tokens);
			if (newName != null) {
				return new SyntaxTreeRenameNode(subquery, newName);
			} else {
				throw new SQLParseException(tokens.getPosition());
			}
		}  else {
			throw new SQLParseException(tokens.getPosition());
		}
	}
	
	private SyntaxTreeNode fromList(SQLTokenStream tokens) throws SQLParseException {
		SyntaxTreeNode right = fromId(tokens);
		SyntaxTreeNode left = optionalConjunctFromList(tokens);
		if (left == null) return right;//in the case case, don't construct a new cross product
		return new SyntaxTreeCrossNode(left, right);//construct the subtree by combining the two children via a cross product
	}
	
	private SyntaxTreeNode optionalConjunctFromList(SQLTokenStream tokens) throws SQLParseException {
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.COMMA) {
			tokens.advance();
			return fromList(tokens);
		} 
		return null;
	}
	
	////
	//Selection (where clause)
	///
	
	private SyntaxTreeNode optionalWhereClause(SQLTokenStream tokens, SyntaxTreeNode subtreeToApplyTo) throws SQLParseException {
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.WHERE) {
			tokens.advance();
			return predicate(tokens, subtreeToApplyTo);
		} 
		return null;
	}
	
	////
	//group by
	////
	
	private SyntaxTreeListNode<SyntaxTreeIdentifierNode> optionalGroupClause(SQLTokenStream tokens) throws SQLParseException {
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.GROUPBY) {
			tokens.advance();
			return listOfConcreteIds(tokens);
		}
		return null;
	}
	
	private SyntaxTreeListNode<SyntaxTreeIdentifierNode> listOfConcreteIds(SQLTokenStream tokens) throws SQLParseException {
		SyntaxTreeIdentifierNode identifier = concreteId(tokens);
		return new SyntaxTreeListNode<SyntaxTreeIdentifierNode>(identifier, optionalConjunctListOfConcreteIds(tokens));
	}
	
	private SyntaxTreeListNode<SyntaxTreeIdentifierNode> optionalConjunctListOfConcreteIds(SQLTokenStream tokens) throws SQLParseException {
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.COMMA) {
			tokens.advance();
			return listOfConcreteIds(tokens);
		}
		return null;
	}
	
	private SyntaxTreeIdentifierNode concreteId(SQLTokenStream tokens) throws SQLParseException {
		SyntaxTreeIdentifierNode node = null;
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.QID ||
			tokens.getTokenClass() == SQLToken.SQLTokenClass.UID) {
			node = new SyntaxTreeIdentifierNode(tokens.getToken());
			tokens.advance();
		} else {
			throw new SQLParseException(tokens.getPosition());
		}
		return node;
	}
	
	////
	//PREDICATES
	////
	
	/**
	 * Builds a tree of SelectionOperatorNodes and appends the subtreeToApplyTo as the last rightmost leaf
	 * @param tokens
	 * @param subtreeToApplyTo
	 */
	private SyntaxTreeNode predicate(SQLTokenStream tokens, SyntaxTreeNode subtreeToApplyTo) throws SQLParseException {
		
		SyntaxTreeIdentifierNode leftValue = comparable(tokens);
		SyntaxTreeIdentifierNode operator = compareOperator(tokens);
		SyntaxTreeIdentifierNode rightValue = comparable(tokens);
		SyntaxTreeNode child = optionalConjunctPredicate(tokens, subtreeToApplyTo);
		if (child != null) {
			return new SyntaxTreeSelectionOperatorNode(leftValue, operator, rightValue, child);
		} else {
			return new SyntaxTreeSelectionOperatorNode(leftValue, operator, rightValue, subtreeToApplyTo);
		}
	}
	
	private SyntaxTreeIdentifierNode compareOperator(SQLTokenStream tokens) throws SQLParseException {
		SyntaxTreeIdentifierNode root;
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.EQUAL ||
			tokens.getTokenClass() == SQLToken.SQLTokenClass.COMPARATOR) {
			root = new SyntaxTreeIdentifierNode(tokens.getToken());
			tokens.advance();
		} else {
			throw new SQLParseException(tokens.getPosition());
		}
		return root;
	}
	
	private SyntaxTreeIdentifierNode comparable(SQLTokenStream tokens) throws SQLParseException {
		SyntaxTreeIdentifierNode root;
		SQLToken.SQLTokenClass token = tokens.getTokenClass();
		if (token == SQLToken.SQLTokenClass.QID ||
			token == SQLToken.SQLTokenClass.UID ||
			token == SQLToken.SQLTokenClass.ARGUMENT ||
			token == SQLToken.SQLTokenClass.LITERAL ||
			token == SQLToken.SQLTokenClass.NUMERIC ||
		    token == SQLToken.SQLTokenClass.BOOL) {
			root = new SyntaxTreeIdentifierNode(tokens.getToken());
			tokens.advance();
		} else {
			throw new SQLParseException(tokens.getPosition());
		}
		return root;
	}
	
	private SyntaxTreeNode optionalConjunctPredicate(SQLTokenStream tokens, SyntaxTreeNode subtreeToApplyTo) throws SQLParseException  {
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.AND) {
			tokens.advance();
			return predicate(tokens, subtreeToApplyTo);
		} else {
			return null;
		}
	}
	
	////
	//UPDATE
	////
	
	private void updateStatement(SQLTokenStream tokens, SyntaxTreeDynamicNode root) throws SQLParseException {
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.UID) {
			root.addChildren(new SyntaxTreeDynamicNode(tokens.getToken()));
			tokens.advance();
			if (tokens.getTokenClass() == SQLToken.SQLTokenClass.SET) {
				root.addChildren(assignmentList(tokens));
				root.addChildren(optionalWhereClause(tokens, null));//TODO: maybe add new SyntaxTreeIdentifierNode as child to where clause
			} else {
				throw new SQLParseException(SQLToken.SQLTokenClass.SET, tokens.getPosition());
			}
		} else {
			throw new SQLParseException(SQLToken.SQLTokenClass.UID, tokens.getPosition());
		}
		
	}

	private SyntaxTreeDynamicNode assignmentList(SQLTokenStream tokens) throws SQLParseException {
		SyntaxTreeDynamicNode root = new SyntaxTreeDynamicNode(tokens.getToken());
		tokens.advance();	
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.UID) {
			root.addChildren(new SyntaxTreeDynamicNode(tokens.getToken()));
			tokens.advance();
			if (tokens.getTokenClass() == SQLToken.SQLTokenClass.EQUAL) {
				root.addChildren(new SyntaxTreeDynamicNode(tokens.getToken()));
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
	
	private SyntaxTreeDynamicNode optionalConjunctAssignmentList(SQLTokenStream tokens) throws SQLParseException {
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.COMMA) {
			//tokens.advance();
			return assignmentList(tokens);
		}
		return null;
	}
	
	////
	//DELETE
	////
	
	private void deleteStatement(SQLTokenStream tokens, SyntaxTreeDynamicNode root) throws SQLParseException {
		if (tokens.getTokenClass() == SQLToken.SQLTokenClass.UID) {
			root.addChildren(new SyntaxTreeDynamicNode(tokens.getToken()));
			tokens.advance();
			if (tokens.getTokenClass() == SQLToken.SQLTokenClass.WHERE) {
				tokens.advance();
				root.addChildren(predicate(tokens, null));//TODO: maybe add new SyntaxTreeIdentifierNode as child to where clause
			} else {
				throw new SQLParseException(SQLToken.SQLTokenClass.WHERE, tokens.getPosition());
			}
		} else {
			throw new SQLParseException(SQLToken.SQLTokenClass.UID, tokens.getPosition());
		}
	}
}
