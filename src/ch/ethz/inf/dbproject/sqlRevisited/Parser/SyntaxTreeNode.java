package ch.ethz.inf.dbproject.sqlRevisited.Parser;

import java.util.ArrayList;
import java.util.List;

public class SyntaxTreeNode {

	protected final SyntaxTreeNode[] children;
	
	SyntaxTreeNode(SyntaxTreeNode...children) {
		this.children = children;
	}
}
