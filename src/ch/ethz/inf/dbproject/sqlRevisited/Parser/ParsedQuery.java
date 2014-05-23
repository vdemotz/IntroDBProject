package ch.ethz.inf.dbproject.sqlRevisited.Parser;

public class ParsedQuery {

	public enum TypeParsedQuery{
		SELECT,
		INSERT,
		DELETE,
		UPDATE
	}
	
	public TypeParsedQuery typeParsedQuery;
	private SyntaxTreeDynamicNode syntaxTreeDynamicNode;
	
	/**
	 * New ParsedQuery
	 * @param t Type of the query
	 * @param s SyntaxTree which represents the query
	 */
	public ParsedQuery(TypeParsedQuery t, SyntaxTreeDynamicNode s){
		this.typeParsedQuery = t;
		this.syntaxTreeDynamicNode = s;
	}
	
	public SyntaxTreeDynamicNode getSyntaxTreeDynamicNode(){
		return this.syntaxTreeDynamicNode;
	}

}
