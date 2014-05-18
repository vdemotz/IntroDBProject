package ch.ethz.inf.dbproject.sqlRevisited.Parser;

import ch.ethz.inf.dbproject.Pair;
import ch.ethz.inf.dbproject.sqlRevisited.TableSchema;

public class SyntaxTreeSelectionOperatorNode extends SyntaxTreeNode {

	public SyntaxTreeIdentifierNode getLeftValue() {
		return (SyntaxTreeIdentifierNode) children.get(0);
	}
	
	public SyntaxTreeIdentifierNode getRightValue() {
		return (SyntaxTreeIdentifierNode) children.get(2);
	}
	
	public SyntaxTreeIdentifierNode getOperator() {
		return (SyntaxTreeIdentifierNode) children.get(1);
	}
	
	public SyntaxTreeNode getChild() {
		return children.get(3);
	}
	
	public final Pair<String, String> leftIdentifier;//if the table schema is not null, and the values are identifiers, this stores the components of the identifier
	public final Pair<String, String> rightIdentifier;

	SyntaxTreeSelectionOperatorNode(SyntaxTreeIdentifierNode leftValue, SyntaxTreeIdentifierNode operator, SyntaxTreeIdentifierNode rightValue, SyntaxTreeNode child) {
		super(leftValue, operator, rightValue, child);
		leftIdentifier =  null;
		rightIdentifier = null;
		assert(leftValue != null);
		assert(operator != null);
		assert(child != null);
	}

	public SyntaxTreeSelectionOperatorNode(TableSchema schema,
			SyntaxTreeIdentifierNode leftValue,
			SyntaxTreeIdentifierNode operator,
			SyntaxTreeIdentifierNode rightValue, SyntaxTreeNode child) throws SQLSemanticException {
		super(schema, leftValue, operator, rightValue, child);
		
		if (getLeftValue().generatingToken.tokenClass == SQLToken.SQLTokenClass.QID || getLeftValue().generatingToken.tokenClass == SQLToken.SQLTokenClass.UID) {
			leftIdentifier = getLeftValue().generatingToken.getFragmentsForIdentifier();
		} else {
			leftIdentifier = null;
		}
		
		if (getRightValue().generatingToken.tokenClass == SQLToken.SQLTokenClass.QID || getRightValue().generatingToken.tokenClass == SQLToken.SQLTokenClass.UID) {
			rightIdentifier = getRightValue().generatingToken.getFragmentsForIdentifier();
		} else {
			rightIdentifier = null;
		}
		
		assert(leftValue != null);
		assert(operator != null);
		assert(child != null);
		assert(schema != null);
	}
	
	/**
	 * @param child
	 * @return a shallow copy of this node, except that the the child is set to newChild
	 * @throws SQLSemanticException 
	 */
	SyntaxTreeSelectionOperatorNode copyWithChild(SyntaxTreeNode newChild) throws SQLSemanticException {
		return new SyntaxTreeSelectionOperatorNode(schema, getLeftValue(), getOperator(), getRightValue(), newChild);
	}
}
