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
	String q1 = "select distinct a from A order by A.a";
	String q2 = "select distinct a, B.b from A, B where A.a = B.b and B.b = A.a order by B.b asc";
	String q3 = "select A.a, count(*), B.b from A, B where A.a = B.b";
	String q4 = "select * from A, B";
	String q5 = "select b.* from B";
	String q6 = "select * from (select count(*), max(A.a) as maximum from A, B) as S";
	String q7 = "select B.a, C.c from (select B.c from B order by c desc) as C, (select * from A where A.a = ?) as B";
	String q8 = "select B.c from A, B, C where a = 5";
	
	String q9 = "select B.c from B, C where B.b=B.c";
	String q10 = "select C.c from B, C where B.b=?";
	String q11 = "select C.c from B, C where ?=b";
	String q12 = "select B.b from B, C where 5=? and ?=C.c and B.c=C.c";
	
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
 	
 	String[] testSucceeds = {q0, q1, q2, q3, q4, q5, q6, q7, q8, q9};
 	List<List<TableSchema>> testSucceedsSchemata = Arrays.asList(ABlist, Alist, ABlist, ABlist, ABlist, ABlist, ABlist, ABlist, ABClist, ABClist);
 	
 	String[] testFails = {q0, q1, q5};
 	List<List<TableSchema>> testFailsSchemata = Arrays.asList(Alist, Blist, Alist);
 	
 	String[] rewriteTests = {q9, q10, q11, q12};
 	
	SQLLexer lex = new SQLLexer();
	SQLParser parser = new SQLParser();
 	
	@Test
	public void testSelectInstantiation() {
		testSucceedsInstantiation(testSucceeds, testSucceedsSchemata);
		testFailsInstantiation(testFails, testFailsSchemata);
	}
	
	@Test
	public void TestSelectRewrite() {
		SQLTokenStream tokens = null;
		String[] testQueries = rewriteTests;
		List<List<TableSchema>> schemata = Arrays.asList(ABClist, ABClist, ABClist, ABClist);
		
		for (int i=0; i<testQueries.length; i++) {
			tokens = new SQLTokenStream(lex.tokenize(testQueries[i]));
			try {
				SyntaxTreeDynamicNode parse = parser.parse(tokens);
				SyntaxTreeNode instanciatedTree = parse.dynamicChildren.get(0).instanciateWithSchemata(schemata.get(i));//infer schema for all nodes in the AST
				SyntaxTreeNode rewrittenTree = instanciatedTree.rewrite();
				
				assertNotNull(instanciatedTree.schema);
				assertNotNull(rewrittenTree);
				assertNotNull(rewrittenTree.schema);
				assertEquals(instanciatedTree.schema, rewrittenTree.instanciateWithSchemata(schemata.get(i)).schema);
				
				System.out.println(instanciatedTree);
				System.out.println(rewrittenTree);
				
			} catch (SQLParseException e) {
				e.printStackTrace();
				fail("unexpected parse error");
			} catch (SQLSemanticException e) {
				e.printStackTrace();
				fail("unexpected semantic exception");
			}
			
		}
		System.out.println();
		
	}
	
	private void testSucceedsInstantiation(String[] testQueries, List<List<TableSchema>> schemata) {
		SQLTokenStream tokens = null;
		
		for (int i=0; i<testQueries.length; i++) {
			try {
				tokens = new SQLTokenStream(lex.tokenize(testQueries[i]));
				SyntaxTreeDynamicNode parse = parser.parse(tokens);
				System.out.println(parse.dynamicChildren.get(0));//print abstract syntax tree
				
				SyntaxTreeNode result = parse.dynamicChildren.get(0).instanciateWithSchemata(schemata.get(i));//infer schema for all nodes in the AST
				assertNotNull(result.schema);
				assertEquals(result.schema, result.instanciateWithSchemata(schemata.get(i)).schema);//inferring twice should give the same result
				System.out.println(result.schema);//print inferred schema
				
			} catch (SQLParseException e) {
				e.printStackTrace();
				fail("unexpected parse error");
			} catch (SQLSemanticException e) {
				e.printStackTrace();
				fail("instanciation failed unexpectedly " + tokens);
			}
		}
		System.out.println();
	}
	
	private void testFailsInstantiation(String[] testQueries, List<List<TableSchema>> schemata) {
		SQLTokenStream tokens;
		
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
		System.out.println();
	}

}
