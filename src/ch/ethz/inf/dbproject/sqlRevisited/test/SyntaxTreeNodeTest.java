package ch.ethz.inf.dbproject.sqlRevisited.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import ch.ethz.inf.dbproject.sqlRevisited.SQLType;
import ch.ethz.inf.dbproject.sqlRevisited.TableSchema;
import ch.ethz.inf.dbproject.sqlRevisited.TableSchemaAttributeDetail;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.SQLLexer;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.SQLParseException;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.SQLParser;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.SQLSemanticException;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.SQLTokenStream;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.SyntaxTreeDynamicNode;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.SyntaxTreeNode;

public class SyntaxTreeNodeTest {

	
	String q0 = "select A.a from A, B where A.a = B.b";
	String q1 = "select distinct A.a from A order by A.a";
	String q2 = "select distinct A.a from A, B where A.a = B.b and B.b = A.a order by B.b asc";
	
	TableSchemaAttributeDetail[] qA = {new TableSchemaAttributeDetail("a", new SQLType(SQLType.BaseType.Integer), true)};
	TableSchemaAttributeDetail[] qB = {new TableSchemaAttributeDetail("b", new SQLType(SQLType.BaseType.Integer), true)};
 	TableSchema qTableA = new TableSchema("a", qA);
 	TableSchema qTableB = new TableSchema("b", qB);
 	
 	String[] testSucceeds = {q0, q1, q2};
 	List<List<TableSchema>> testSucceedsSchemata = Arrays.asList(Arrays.asList(qTableA, qTableB), Arrays.asList(qTableA), Arrays.asList(qTableA, qTableB));
 	
 	String[] testFails = {q0, q1};
 	List<List<TableSchema>> testFailsSchemata = Arrays.asList(Arrays.asList(qTableA), Arrays.asList(qTableB));
 	
	@Test
	public void testSelect() {
		testSucceedsInstantiation(testSucceeds, testSucceedsSchemata);
		testFailsInstantiation(testFails, testFailsSchemata);
	}
	
	private void testSucceedsInstantiation(String[] testQueries, List<List<TableSchema>> schemata) {
		SQLLexer lex = new SQLLexer();
		SQLParser parser = new SQLParser();
		SQLTokenStream tokens = null;
		int parsedUntil;
		
		for (int i=0; i<testQueries.length; i++) {
			try {
				tokens = new SQLTokenStream(lex.tokenize(testQueries[i]));
				SyntaxTreeDynamicNode parse = parser.parse(tokens);
				System.out.println(parse.dynamicChildren.get(0));
				parse.dynamicChildren.get(0).instanciateWithSchemata(schemata.get(i));
				
			} catch (SQLParseException e) {
				e.printStackTrace();
				fail("unexpected parse error");
			} catch (SQLSemanticException e) {
				e.printStackTrace();
				fail("instanciation failed unexpectedly " + tokens);
			}
		}
	}
	
	private void testFailsInstantiation(String[] testQueries, List<List<TableSchema>> schemata) {
		SQLLexer lex = new SQLLexer();
		SQLParser parser = new SQLParser();
		SQLTokenStream tokens;
		int parsedUntil;
		
		for (int i=0; i<testQueries.length; i++) {
			try {
				tokens = new SQLTokenStream(lex.tokenize(testQueries[i]));
				SyntaxTreeDynamicNode parse = parser.parse(tokens);
				System.out.println(parse.dynamicChildren.get(0));
				parse.dynamicChildren.get(0).instanciateWithSchemata(schemata.get(i));
				fail("instanciation succeeded unexpectedly " + tokens);
			} catch (SQLParseException e) {
				e.printStackTrace();
				fail("unexpected parse error");
			} catch (SQLSemanticException e) {
			}
		}
	}

}
