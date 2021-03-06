package ch.ethz.inf.dbproject.sqlRevisited.Parser;

public class SyntaxTreeListNode<T extends SyntaxTreeNode> extends SyntaxTreeNode {

	@SuppressWarnings("unchecked")
	public SyntaxTreeListNode<T> getNext() {
		return (SyntaxTreeListNode<T>)children.get(0);
	}
	
	@SuppressWarnings("unchecked")
	public T getNode() {
		return (T)children.get(1);
	}
	
	public SyntaxTreeListNode(T node, SyntaxTreeListNode<T> next) {
		super(next, node);
	}
	
}
