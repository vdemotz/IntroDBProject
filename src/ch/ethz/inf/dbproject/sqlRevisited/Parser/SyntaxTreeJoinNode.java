package ch.ethz.inf.dbproject.sqlRevisited.Parser;

import ch.ethz.inf.dbproject.Pair;
import ch.ethz.inf.dbproject.sqlRevisited.TableSchema;

public class SyntaxTreeJoinNode extends SyntaxTreeBinaryNode {

	public final Pair<String, String> leftIdentifier;
	public final Pair<String, String> rightIdentifier;
	
	SyntaxTreeJoinNode(TableSchema schema,  SyntaxTreeNode left, SyntaxTreeNode right, Pair<String, String> leftId, Pair<String, String> rightId) {
		super(schema, left, right);
		assert(left.schema.hasAttribute(leftId));
		assert(right.schema.hasAttribute(rightId));
		leftIdentifier = leftId;
		rightIdentifier = rightId;
	}
	
	public SyntaxTreeBinaryNode copyWithChildren(SyntaxTreeNode left, SyntaxTreeNode right) {
		return new SyntaxTreeJoinNode(schema, left, right, leftIdentifier, rightIdentifier);
	}
	
}
