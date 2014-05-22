package ch.ethz.inf.dbproject.sqlRevisited.Parser;

import ch.ethz.inf.dbproject.Pair;
import ch.ethz.inf.dbproject.sqlRevisited.TableSchema;

public class SyntaxTreeAggregateNode extends SyntaxTreeNode {

	public final SQLToken generatingToken;
	public final String aggregateName;//the (new) name of the aggregate attribute
	public final Pair<String, String> attributeIdentifier; //identifier of the attribute the aggregation is being applied to;
	
	SyntaxTreeNode child() {
		return children.get(0);
	}
	
	public SyntaxTreeAggregateNode(TableSchema schema, SQLToken token, String aggregateName, SyntaxTreeNode child) {
		super(schema, child);
		generatingToken = token;
		this.aggregateName = aggregateName;
		String nameOfColumn = generatingToken.content.split("[\\(]|[\\)]")[1];
		attributeIdentifier = SQLToken.getFragmentsForIdentifier(nameOfColumn);
	}
	
	public SyntaxTreeAggregateNode(SQLToken token, String aggregateName, SyntaxTreeNode child) {
		super(child);
		generatingToken = token;
		this.aggregateName = aggregateName;
		String nameOfColumn = generatingToken.content.split("[\\(]|[\\)]")[1];
		attributeIdentifier = SQLToken.getFragmentsForIdentifier(nameOfColumn);
	}
	
	@Override
	protected String infoToString()
	{
		return generatingToken.content.toUpperCase();
	}
	
}
