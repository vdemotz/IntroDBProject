package ch.ethz.inf.dbproject.sqlRevisited;

public class SQLAbstractSyntaxTree {
	
	//Represents an SQL statement as an (immutable) tree
	ASTNode root;
	
	public SQLAbstractSyntaxTree() {}
	
	public SQLAbstractSyntaxTree(ASTNode root) {
		this.root = root;
	}
	
	public void setRoot(ASTNode root){
		this.root = root;
	}
}
