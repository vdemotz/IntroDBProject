package ch.ethz.inf.dbproject.sqlRevisited.Parser;

public class SyntaxTreeListNode<T extends SyntaxTreeNode> extends SyntaxTreeNode {

	@SuppressWarnings("unchecked")
	public T getNext() {
		return (T)children[0];
	}
	
	@SuppressWarnings("unchecked")
	public T getNode() {
		return (T)children[1];
	}
	
	public SyntaxTreeListNode(T node, SyntaxTreeListNode<T> next) {
		super(next, node);
	}
	
}
