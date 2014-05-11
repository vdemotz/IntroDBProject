package ch.ethz.inf.dbproject.sqlRevisited.Parser;


public class SQLAbstractSyntaxTree {
	
	//Represents an SQL statement as a tree
	//TODO: this class is probably unnecessary
	
	SyntaxTreeDynamicNode root;
	
	public SQLAbstractSyntaxTree() {}
	
	public SQLAbstractSyntaxTree(SyntaxTreeDynamicNode root) {
		this.root = root;
	}
	
	public void setRoot(SyntaxTreeDynamicNode root){
		this.root = root;
	}
}
