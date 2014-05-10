package ch.ethz.inf.dbproject.sqlRevisited;

public class SQLSyntaxTreeIdentifierListNode extends SyntaxTreeNode {

	SQLSyntaxTreeIdentifierListNode getNext() {
		return (SQLSyntaxTreeIdentifierListNode)children[0];
	}
	
	SQLSyntaxTreeIdentifierNode getIdentifier() {
		return (SQLSyntaxTreeIdentifierNode)children[1];
	}
	
	SQLSyntaxTreeIdentifierListNode(SQLSyntaxTreeIdentifierListNode next, SQLSyntaxTreeIdentifierNode identifier) {
		super(next, identifier);
	}
	
}
