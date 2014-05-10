package ch.ethz.inf.dbproject.sqlRevisited.Parser;


public class SQLAbstractSyntaxTree {
	
	//Represents an SQL statement as a tree
	//TODO: this class is probably unnecessary
	
	ASTNode root;
	
	public SQLAbstractSyntaxTree() {}
	
	public SQLAbstractSyntaxTree(ASTNode root) {
		this.root = root;
	}
	
	public void setRoot(ASTNode root){
		this.root = root;
	}
}
