package ch.ethz.inf.dbproject.sqlRevisited.Parser;

public class SyntaxTreeSelectionOperatorNode extends SyntaxTreeNode {

	SyntaxTreeSelectionOperatorNode(SyntaxTreeIdentifierNode leftValue, SyntaxTreeIdentifierNode operator, SyntaxTreeIdentifierNode rightValue, SyntaxTreeNode child) {
		super(leftValue, operator, rightValue, child);
	}
	
}
