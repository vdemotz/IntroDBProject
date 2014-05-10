package ch.ethz.inf.dbproject.sqlRevisited.Parser;

public class SyntaxTreeGroupByNode extends SyntaxTreeNode {

	SyntaxTreeNode child() {
		return children[0];
	}
	
	@SuppressWarnings("unchecked")
	SyntaxTreeListNode<SyntaxTreeIdentifierNode> groupByList(){
		return (SyntaxTreeListNode<SyntaxTreeIdentifierNode>) children[1];
	}
	
	public SyntaxTreeGroupByNode(SyntaxTreeNode result, SyntaxTreeListNode<SyntaxTreeIdentifierNode> groupByList) {
		super(result, groupByList);
	}

}
