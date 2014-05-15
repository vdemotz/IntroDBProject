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
	String q2 = "select distinct A.a, B.b from A, B where A.a = B.b and B.b = A.a order by B.b asc";
	String q3 = "select A.a, count(*), B.b from A, B where A.a = B.b";
	String q4 = "select * from A, B";
	String q5 = "select b.* from B";
	String q6 = "select * from (select count(*), max(A.a) as maximum from A, B) as S";
	String q7 = "select B.a, C.c from (select B.c from B order by c desc) as C, (select * from A where A.a = ?) as B";
	String q8 = "select B.c from A, B, C";
	
	TableSchemaAttributeDetail[] qA = {new TableSchemaAttributeDetail("a", new SQLType(SQLType.BaseType.Integer), true)};
	TableSchemaAttributeDetail[] qB = {new TableSchemaAttributeDetail("b", new SQLType(SQLType.BaseType.Char, 8), true), new TableSchemaAttributeDetail("c", new SQLType(SQLType.BaseType.Datetime), true)};
	TableSchemaAttributeDetail[] qC = {new TableSchemaAttributeDetail("c", new SQLType(SQLType.BaseType.Date), true)};
	
	TableSchema qTableA = new TableSchema("a", qA);
 	TableSchema qTableB = new TableSchema("b", qB);
 	TableSchema qTableC = new TableSchema("c", qC);
 	
	List<TableSchema> Alist = Arrays.asList(qTableA);
	List<TableSchema> Blist = Arrays.asList(qTableB);
	List<TableSchema> ABlist = Arrays.asList(qTableA,qTableB);
	List<TableSchema> ABClist = Arrays.asList(qTableA,qTableB,qTableC);
 	
 	String[] testSucceeds = {q0, q1, q2, q3, q4, q5, q6, q7, q8};
 	List<List<TableSchema>> testSucceedsSchemata = Arrays.asList(ABlist, Alist, ABlist, ABlist, ABlist, ABlist, ABlist, ABlist, ABClist);
 	
 	String[] testFails = {q0, q1, q5};
 	List<List<TableSchema>> testFailsSchemata = Arrays.asList(Alist, Blist, Alist);
 	
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
				System.out.println(parse.dynamicChildren.get(0));//print abstract syntax tree
				
				SyntaxTreeNode result = parse.dynamicChildren.get(0).instanciateWithSchemata(schemata.get(i));//infer schema for all nodes in the AST
				assertNotNull(result.schema);
				System.out.println(result.schema);//print inferred schema
				
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
				parse.dynamicChildren.get(0).instanciateWithSchemata(schemata.get(i));
				System.out.println(parse.dynamicChildren.get(0));
				fail("instanciation succeeded unexpectedly " + tokens);
			} catch (SQLParseException e) {
				e.printStackTrace();
				fail("unexpected parse error");
			} catch (SQLSemanticException e) {
			}
		}
	}

}
