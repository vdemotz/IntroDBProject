package ch.ethz.inf.dbproject.sqlRevisited.Parser;

import ch.ethz.inf.dbproject.sqlRevisited.AttributedTableSchema;

public class SyntaxTreeGroupByNode extends SyntaxTreeNode {

	SyntaxTreeNode child() {
		return children[0];
	}
	
	@SuppressWarnings("unchecked")
	SyntaxTreeListNode<SyntaxTreeIdentifierNode> groupByList(){
		return (SyntaxTreeListNode<SyntaxTreeIdentifierNode>) children[1];
	}
	
	public SyntaxTreeGroupByNode(SyntaxTreeNode child, SyntaxTreeListNode<SyntaxTreeIdentifierNode> groupByList) {
		super(child, groupByList);
	}

	public SyntaxTreeGroupByNode(AttributedTableSchema schema, SyntaxTreeNode child, SyntaxTreeListNode<SyntaxTreeIdentifierNode> groupByList) {
		super(schema, child, groupByList);
	}

}
