package ch.ethz.inf.dbproject.sqlRevisited.Parser;

import java.util.ArrayList;


public class ASTNode {

	//Represents a node in an SQLAbstractSyntaxTree
	
	private SQLToken token;
	private ArrayList<ASTNode> children;
	
	/**
	 * Create a new node with empty ArrayList of child and a (immutable) token
	 * @param token
	 */
	public ASTNode(SQLToken token) {
		this.token = token;
		children = new ArrayList<ASTNode>();
	}
	
	/**
	 * Get the token of the node
	 * @return token
	 */
	public SQLToken getToken() {
		return token;
	}
	
	/**
	 * Add a child to the node if a_child not null
	 * @param a_child
	 */
	public void addChildren(ASTNode a_child) {
		if (a_child == null) { return; }
		this.children.add(a_child);
	}	
}
