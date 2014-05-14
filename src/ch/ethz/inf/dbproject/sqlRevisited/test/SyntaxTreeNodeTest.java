package ch.ethz.inf.dbproject.sqlRevisited.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
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
	
	TableSchemaAttributeDetail[] qA = {new TableSchemaAttributeDetail("a", new SQLType(SQLType.BaseType.Integer), true)};
	TableSchemaAttributeDetail[] qB = {new TableSchemaAttributeDetail("b", new SQLType(SQLType.BaseType.Integer), true)};
 	TableSchema qTableA = new TableSchema("a", qA);
 	TableSchema qTableB = new TableSchema("b", qB);
 	
	@Test
	public void test() {
		
		SQLLexer lex = new SQLLexer();
		SQLParser parser = new SQLParser();
		SQLTokenStream tokens = new SQLTokenStream(lex.tokenize(q0));
		
		try {
			List<TableSchema> schemata = new ArrayList<TableSchema>();
			schemata.add(qTableA);
			schemata.add(qTableB);
			SyntaxTreeDynamicNode parse = parser.parse(tokens);
			System.out.println(parse.dynamicChildren.get(0));
			parse.dynamicChildren.get(0).instanciateWithSchemata(schemata);
			
			
		} catch (SQLParseException e) {
			e.printStackTrace();
			fail("unexpected parse error");
		} catch (SQLSemanticException e) {
			e.printStackTrace();
			fail("unexpected semantic error");
		}
		
		
		
		fail("Not yet implemented");
	}

}
