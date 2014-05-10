package ch.ethz.inf.dbproject.sqlRevisited;

public class SQLAbstractSyntaxTree {
	
	//Represents an SQL statement as a tree
	ASTNode root;
	
	public SQLAbstractSyntaxTree() {}
	
	public SQLAbstractSyntaxTree(ASTNode root) {
		this.root = root;
	}
	
	public void setRoot(ASTNode root){
		this.root = root;
	}
}
