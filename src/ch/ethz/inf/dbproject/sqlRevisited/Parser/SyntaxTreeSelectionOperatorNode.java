package ch.ethz.inf.dbproject.sqlRevisited.Parser;

import ch.ethz.inf.dbproject.sqlRevisited.TableSchema;

public class SyntaxTreeSelectionOperatorNode extends SyntaxTreeNode {

	SyntaxTreeIdentifierNode getLeftValue()
	{
		return (SyntaxTreeIdentifierNode) children[0];
	}
	
	SyntaxTreeIdentifierNode getRightValue()
	{
		return (SyntaxTreeIdentifierNode) children[1];
	}
	
	SyntaxTreeIdentifierNode getOperator()
	{
		return (SyntaxTreeIdentifierNode) children[2];
	}
	
	SyntaxTreeNode getChild()
	{
		return children[0];
	}
	
	SyntaxTreeSelectionOperatorNode(SyntaxTreeIdentifierNode leftValue, SyntaxTreeIdentifierNode operator, SyntaxTreeIdentifierNode rightValue, SyntaxTreeNode child) {
		super(leftValue, operator, rightValue, child);
		assert(leftValue != null);
		assert(operator != null);
		assert(child != null);
	}

	public SyntaxTreeSelectionOperatorNode(TableSchema schema,
			SyntaxTreeIdentifierNode leftValue,
			SyntaxTreeIdentifierNode operator,
			SyntaxTreeIdentifierNode rightValue, SyntaxTreeNode child) {
		super(schema, leftValue, operator, rightValue, child);
		assert(leftValue != null);
		assert(operator != null);
		assert(child != null);
		assert(schema != null);
	}
	
	/**
	 * @param child
	 * @return a shallow copy of this node, except that the the child is set to newChild
	 */
	SyntaxTreeSelectionOperatorNode copyWithChild(SyntaxTreeNode newChild) {
		return new SyntaxTreeSelectionOperatorNode(schema, getLeftValue(), getOperator(), getRightValue(), newChild);
	}
}
