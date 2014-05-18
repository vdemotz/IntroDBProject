package ch.ethz.inf.dbproject.sqlRevisited;

import java.util.Map;

import ch.ethz.inf.dbproject.sqlRevisited.Parser.*;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.SyntaxTreeNode.*;

public class SQLCodegen {

	SQLOperator generateSelectStatement(SyntaxTreeNode node, Object ... arguments) throws SQLSemanticException {
		return node.fold(new GeneratorBase(), new GeneratorCross(), new GeneratorJoin(), new GeneratorGroup(), new GeneratorDistinct(),
						 new GeneratorProjectAndAggregate(), new GeneratorRename(), new GeneratorSelection(), new GeneratorSort());	
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
		@Override
		public SQLOperator transform(SyntaxTreeSelectionOperatorNode currentNode, SQLOperator childResult) throws SQLSemanticException {
			// TODO resolve predicate
			// TODO instantiate appropriate selection operator (scan, indexed)
			return null;
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
