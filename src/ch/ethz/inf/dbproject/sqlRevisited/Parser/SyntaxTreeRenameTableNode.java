package ch.ethz.inf.dbproject.sqlRevisited.Parser;

public class SyntaxTreeRenameTableNode extends SyntaxTreeNode {
	
	String name;
	
	SyntaxTreeRenameTableNode(SyntaxTreeNode table, String newName) {
		super(table);
		this.name = newName;
	}
	
}
