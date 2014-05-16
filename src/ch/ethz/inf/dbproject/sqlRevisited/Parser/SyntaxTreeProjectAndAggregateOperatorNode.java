package ch.ethz.inf.dbproject.sqlRevisited.Parser;


import java.util.LinkedList;
import java.util.List;

import ch.ethz.inf.dbproject.Pair;
import ch.ethz.inf.dbproject.sqlRevisited.SQLType;
import ch.ethz.inf.dbproject.sqlRevisited.TableSchema;
import ch.ethz.inf.dbproject.sqlRevisited.TableSchemaAttributeDetail;

public class SyntaxTreeProjectAndAggregateOperatorNode extends SyntaxTreeNode {
	
	@SuppressWarnings("unchecked")
	SyntaxTreeListNode<SyntaxTreeNode> getProjectionList()
	{
		return (SyntaxTreeListNode<SyntaxTreeNode>) children.get(1);
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
		if (node.getClass().equals(SyntaxTreeRenameTableNode.class)) {//Case 0 :: renamed aggregate
			SyntaxTreeRenameTableNode rdnode = (SyntaxTreeRenameTableNode)node;
			if (rdnode.getChild().getClass().equals(SyntaxTreeIdentifierNode.class)) {
				SyntaxTreeIdentifierNode inode = (SyntaxTreeIdentifierNode)rdnode.getChild();
				result.add(new TableSchemaAttributeDetail(rdnode.name, new SQLType(SQLType.BaseType.Integer), false));
			} else {
				throw new SQLSemanticException(SQLSemanticException.Type.InternalError);
			}
			
		} else if (node.getClass().equals(SyntaxTreeIdentifierNode.class)){//Case 1 :: identifier or unnamed aggregate
			SyntaxTreeIdentifierNode idnode = (SyntaxTreeIdentifierNode)node;
			if (idnode.generatingToken.tokenClass == SQLToken.SQLTokenClass.STAR) {//Case 1a ::add all attributes from the child schema
				result = schema.getAttributes();
				
			} else if (idnode.generatingToken.tokenClass == SQLToken.SQLTokenClass.QSTARID) {//Case 1b :: add all attributes that have the right qualifier
				List<TableSchemaAttributeDetail> allAttributes = schema.getAttributes();
				String attributeQualifier = idnode.generatingToken.getQualifierForIdentifier();
				if (!schema.getQualifiers().contains(attributeQualifier)) {
					throw new SQLSemanticException(SQLSemanticException.Type.NoSuchTableException, attributeQualifier);
				}
				
				for (TableSchemaAttributeDetail attribute : allAttributes) {
					if (attribute.qualifier.equals(attributeQualifier)) {
						result.add(attribute);
					}
				}
				
			} else if (idnode.generatingToken.tokenClass == SQLToken.SQLTokenClass.QID) {//Case 1c :: add the first attribute that has the right name and right qualifier
				Pair<String, String> nameParts = idnode.generatingToken.getFragmentsForIdentifier();
				int currentIndex = schema.indexOfQualifiedAttributeName(nameParts.first, nameParts.second);
				result.add(schema.getAttributes().get(currentIndex));
				
			} else if (idnode.generatingToken.tokenClass == SQLToken.SQLTokenClass.UID) {//Case 1d :: add the first attribute that has the right name
				Pair<String, String> nameParts = idnode.generatingToken.getFragmentsForIdentifier();
				int index = schema.indexOfAttributeName(nameParts.second, 0);
				result.add(schema.getAttributes().get(index));
				
			} else if (idnode.generatingToken.tokenClass == SQLToken.SQLTokenClass.AGGREGATE) {//Case 1d unnamed aggregate :: add a new aggregate attribute without renaming
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
	
}
