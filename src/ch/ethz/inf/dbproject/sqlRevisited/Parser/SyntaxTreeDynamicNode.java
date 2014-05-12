package ch.ethz.inf.dbproject.sqlRevisited.Parser;

import java.util.ArrayList;


public class SyntaxTreeDynamicNode extends SyntaxTreeIdentifierNode {

	//Represents a node in an SQLAbstractSyntaxTree
	
	private ArrayList<SyntaxTreeNode> dynamicChildren;
	
	/**
	 * Create a new node with empty ArrayList of child and a (immutable) token
	 * @param token
	 */
	public SyntaxTreeDynamicNode(SQLToken token) {
		super(token);
		dynamicChildren = new ArrayList<SyntaxTreeNode>();
	}
	
	/**
	 * Get the token of the node
	 * @return token
	 */
	public SQLToken getToken() {
		return generatingToken;
	}
	
	/**
	 * Add a child to the node if a_child not null
	 * @param a_child
	 */
	public void addChildren(SyntaxTreeNode a_child) {
		if (a_child == null) {
			return;
		}
		this.dynamicChildren.add(a_child);
	}	
}
