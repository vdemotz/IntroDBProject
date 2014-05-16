package ch.ethz.inf.dbproject.sqlRevisited.Parser;

import ch.ethz.inf.dbproject.sqlRevisited.TableSchema;

public class SyntaxTreeSortOperatorNode extends SyntaxTreeNode {

	
	@SuppressWarnings("unchecked")
	SyntaxTreeListNode<SyntaxTreeOrderingNode> getOrderStatement()
	{
		return (SyntaxTreeListNode<SyntaxTreeOrderingNode>) children.get(1);
	}
	
	public SyntaxTreeSortOperatorNode(SyntaxTreeNode innerStatement, SyntaxTreeListNode<SyntaxTreeOrderingNode> orderStatement) {
		super(innerStatement, orderStatement);
	}

	public SyntaxTreeSortOperatorNode(TableSchema schema, SyntaxTreeNode child, SyntaxTreeListNode<SyntaxTreeOrderingNode> orderStatement) {
		super(schema, child, orderStatement);
		assert(schema != null);
	}

}
