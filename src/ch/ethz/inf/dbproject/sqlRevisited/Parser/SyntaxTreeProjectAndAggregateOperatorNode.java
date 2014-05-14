package ch.ethz.inf.dbproject.sqlRevisited.Parser;

import java.util.LinkedList;
import java.util.List;

import ch.ethz.inf.dbproject.sqlRevisited.SQLType;
import ch.ethz.inf.dbproject.sqlRevisited.TableSchema;
import ch.ethz.inf.dbproject.sqlRevisited.TableSchemaAttributeDetail;

public class SyntaxTreeProjectAndAggregateOperatorNode extends SyntaxTreeNode {
	
	@SuppressWarnings("unchecked")
	SyntaxTreeListNode<SyntaxTreeNode> getProjectionList()
	{
		return (SyntaxTreeListNode<SyntaxTreeNode>) children[1];
	}
	
	public SyntaxTreeProjectAndAggregateOperatorNode(SyntaxTreeNode child, SyntaxTreeListNode<SyntaxTreeNode> projectOnto) {
		super(child, projectOnto);
		assert(child != null);
		assert(projectOnto != null);
	}

	public SyntaxTreeProjectAndAggregateOperatorNode(TableSchema schema, SyntaxTreeNode child, SyntaxTreeListNode<SyntaxTreeNode> projectionList) {
		super(schema, child, projectionList);
		assert(schema != null);
		assert(child != null);
		assert(projectionList != null);
	}
	
	public static List<TableSchemaAttributeDetail> resolve(SyntaxTreeListNode<SyntaxTreeNode> projectionList, TableSchema schema) throws SQLSemanticException {
		SyntaxTreeNode node = projectionList.getNode();
		List<TableSchemaAttributeDetail> result = new LinkedList<TableSchemaAttributeDetail>();
		if (node.getClass().equals(SyntaxTreeRenameTableNode.class)) {
			//TODO
		} else if (node.getClass().equals(SyntaxTreeIdentifierNode.class)){
			SyntaxTreeIdentifierNode idnode = (SyntaxTreeIdentifierNode)node;
			if (idnode.generatingToken.tokenClass == SQLToken.SQLTokenClass.STAR) {//add all attributes from the child schema
				result = schema.getAttributes();
				
			} else if (idnode.generatingToken.tokenClass == SQLToken.SQLTokenClass.QSTARID) {//add all attributes that have the right qualifier
				List<TableSchemaAttributeDetail> allAttributes = schema.getAttributes();
				String attributeQualifier = getQualifierForIdentifier(idnode.generatingToken);
				for (TableSchemaAttributeDetail attribute : allAttributes) {
					if (attribute.qualifier.equals(attributeQualifier)) {
						result.add(attribute);
					}
				}
				
			} else if (idnode.generatingToken.tokenClass == SQLToken.SQLTokenClass.QID) {//add the first attribute that has the right name and right qualifier
				String[] nameParts = getFragmentsForIdentifier(idnode.generatingToken);
				int currentIndex = schema.indexOfQualifiedAttributeName(nameParts[0], nameParts[1]);
				result.add(schema.getAttributes().get(currentIndex));
				
			} else if (idnode.generatingToken.tokenClass == SQLToken.SQLTokenClass.UID) {//add the first attribute that has the right name
				String[] nameParts = getFragmentsForIdentifier(idnode.generatingToken);
				int index = schema.indexOfAttributeName(nameParts[1], 0);
				result.add(schema.getAttributes().get(index));
				
			} else if (idnode.generatingToken.tokenClass == SQLToken.SQLTokenClass.AGGREGATE) {//add a new aggregate attribute without renaming
				result.add(new TableSchemaAttributeDetail(idnode.generatingToken.content, new SQLType(SQLType.BaseType.Integer), false));
				
			} else {
				throw new SQLSemanticException(SQLSemanticException.Type.InternalError);
			}
			
		} else {
			throw new SQLSemanticException(SQLSemanticException.Type.InternalError);
		}
		
		if (projectionList.getNext() != null) {//recursively resolve rest of the list and append
			result.addAll(resolve(projectionList.getNext(), schema));
		}
		
		return result;
	}
	
	private static String getQualifierForIdentifier(SQLToken token) {
		return token.content.split("\\.", 1)[0];
	}
	
	private static String[] getFragmentsForIdentifier(SQLToken token) {
		return token.content.split("\\.", 2);
	}
	
	
}
