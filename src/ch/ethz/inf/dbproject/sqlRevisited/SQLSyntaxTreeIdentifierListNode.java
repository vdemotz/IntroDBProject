package ch.ethz.inf.dbproject.sqlRevisited;

public class SQLSyntaxTreeIdentifierListNode extends SyntaxTreeNode {

	public SQLSyntaxTreeIdentifierListNode getNext() {
		return (SQLSyntaxTreeIdentifierListNode)children[0];
	}
	
	public SQLSyntaxTreeIdentifierNode getIdentifier() {
		return (SQLSyntaxTreeIdentifierNode)children[1];
	}
	
	public SQLSyntaxTreeIdentifierListNode(SQLSyntaxTreeIdentifierListNode next, SQLSyntaxTreeIdentifierNode identifier) {
		super(next, identifier);
	}
	
}
