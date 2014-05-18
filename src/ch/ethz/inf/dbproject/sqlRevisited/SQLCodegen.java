package ch.ethz.inf.dbproject.sqlRevisited;

import java.util.Map;

import ch.ethz.inf.dbproject.Pair;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.*;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.SyntaxTreeNode.*;

public class SQLCodegen {

	SQLOperator generateSelectStatement(SyntaxTreeNode node, Object ... arguments) throws SQLSemanticException {
		return node.fold(new GeneratorBase(), new GeneratorCross(), new GeneratorJoin(arguments), new GeneratorGroup(), new GeneratorDistinct(),
						 new GeneratorProjectAndAggregate(), new GeneratorRename(), new GeneratorSelection(arguments), new GeneratorSort());	
	}
	
	////
	//CODE GENERATION :: IMPLEMENTATION
	////
	
	class GeneratorBase implements TransformBase<SyntaxTreeBaseRelationNode, SQLOperator>
	{
		@Override
		public SQLOperator transform(SyntaxTreeBaseRelationNode currentNode) throws SQLSemanticException {
			// TODO get TableConnection from Database
			// TODO instantiate SQLOperatorBase with TableConnection and schema
			return null;
		}
	}
	
	class GeneratorCross implements TransformBinary<SyntaxTreeCrossNode, SQLOperator>
	{
		@Override
		public SQLOperator transform(SyntaxTreeCrossNode currentNode, SQLOperator leftChildResult, SQLOperator rightChildResult) throws SQLSemanticException {
			return new SQLOperatorCross(currentNode.schema, leftChildResult, rightChildResult);
		}
	}
	
	class GeneratorJoin implements TransformBinary<SyntaxTreeJoinNode, SQLOperator>
	{
		
		public GeneratorJoin(Object[] arguments) {
			// TODO Auto-generated constructor stub
		}

		@Override
		public SQLOperator transform(SyntaxTreeJoinNode currentNode, SQLOperator leftChildResult, SQLOperator rightChildResult) throws SQLSemanticException {
			// TODO resolve predicate
			// TODO instantiate appropriate join operator (nested loops, index join, ...)
			return null;
		}
	}
	
	class GeneratorGroup implements TransformUnary<SyntaxTreeGroupByNode, SQLOperator>
	{
		@Override
		public SQLOperator transform(SyntaxTreeGroupByNode currentNode, SQLOperator childResult) throws SQLSemanticException {
			// TODO resolve grouping list
			// TODO instantiate group operator
			return null;
		}
	}
	
	class GeneratorDistinct implements TransformUnary<SyntaxTreeNodeDistinct, SQLOperator>
	{
		@Override
		public SQLOperator transform(SyntaxTreeNodeDistinct currentNode, SQLOperator childResult) throws SQLSemanticException {
			// TODO Reduce to grouping, projecting onto self
			return null;
		}
	}
	
	class GeneratorProjectAndAggregate implements TransformUnary<SyntaxTreeProjectAndAggregateOperatorNode, SQLOperator>
	{
		@Override
		public SQLOperator transform(SyntaxTreeProjectAndAggregateOperatorNode currentNode, SQLOperator childResult) throws SQLSemanticException {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	class GeneratorRename implements TransformUnary<SyntaxTreeRenameTableNode, SQLOperator>
	{
		@Override
		public SQLOperator transform(SyntaxTreeRenameTableNode currentNode, SQLOperator childResult) throws SQLSemanticException {
			// TODO Copy childResult with new schema
			return null;
		}
	}
	
	class GeneratorSelection implements TransformUnary<SyntaxTreeSelectionOperatorNode, SQLOperator>
	{
		private final Object[] arguments;
		
		public GeneratorSelection(Object[] arguments) {
			this.arguments = arguments;
		}

		@Override
		public SQLOperator transform(SyntaxTreeSelectionOperatorNode currentNode, SQLOperator childResult) throws SQLSemanticException {
			
			// TODO resolve predicate
			Predicate<byte[]> predicate;
			if (currentNode.leftIdentifier != null && currentNode.rightIdentifier != null) {
				predicate = resolveTwoSidedPredicate(currentNode.schema, currentNode.leftIdentifier, currentNode.rightIdentifier, currentNode.getOperator().generatingToken);
				
			} else if (currentNode.leftIdentifier == null && currentNode.rightIdentifier != null) {
				predicate = resolveOneSidedPredicate(currentNode.schema, currentNode.getLeftValue().generatingToken, currentNode.rightIdentifier, currentNode.getOperator().generatingToken);
				
			} else if (currentNode.leftIdentifier != null && currentNode.rightIdentifier == null) {
				predicate = resolveOneSidedPredicate(currentNode.schema, currentNode.getRightValue().generatingToken, currentNode.leftIdentifier, currentNode.getOperator().generatingToken);
				
			} else {
				predicate = resolveZeroSidedPredicate(currentNode.getLeftValue().generatingToken, currentNode.getRightValue().generatingToken, currentNode.getOperator().generatingToken);
				
			}
			
			// TODO instantiate appropriate selection operator (scan, indexed)
			return null;
		}
		
		private Predicate<byte[]> resolveZeroSidedPredicate(SQLToken leftValue, SQLToken rightValue, SQLToken operatorToken) throws SQLSemanticException {
			// TODO Auto-generated method stub
			Object leftConstant = resolveConstant(leftValue);
			Object rightConstant = resolveConstant(rightValue);
			
			return null;
		}
		
		private Predicate<byte[]> resolveOneSidedPredicate(TableSchema schema, SQLToken literalOrArgumentToken, Pair<String, String> identifier, SQLToken operatorToken) {
			return null;
		}
		
		private Predicate<byte[]> resolveTwoSidedPredicate(TableSchema schema, Pair<String, String> leftIdentifier, Pair<String, String> rightIdentifier, SQLToken operatorToken) {
			// TODO Auto-generated method stub
			return null;
		}
		
		private Object resolveConstant(SQLToken token) throws SQLSemanticException {
			if (token.tokenClass == SQLToken.SQLTokenClass.ARGUMENT) {
				return arguments[token.identifier];
			} else if (token.tokenClass == SQLToken.SQLTokenClass.LITERAL) {
				return token.content;
			} else if (token.tokenClass == SQLToken.SQLTokenClass.NUMERIC) {
				return token.identifier;
			} else if (token.tokenClass == SQLToken.SQLTokenClass.BOOL) {
				if (token.content.equals("true")) {
					return true;
				} else {
					return false;
				}
			} else {
				throw new SQLSemanticException(SQLSemanticException.Type.InternalError);
			}
		}

	}
	
	class GeneratorSort implements TransformUnary<SyntaxTreeSortOperatorNode, SQLOperator>
	{
		@Override
		public SQLOperator transform(SyntaxTreeSortOperatorNode currentNode, SQLOperator childResult) throws SQLSemanticException {
			// TODO resolve sort relation
			// TODO instantiate appropriate sort operator
			return null;
		}
	}
}
