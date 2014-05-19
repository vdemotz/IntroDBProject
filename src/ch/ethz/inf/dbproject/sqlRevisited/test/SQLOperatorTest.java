package ch.ethz.inf.dbproject.sqlRevisited.test;

import static org.junit.Assert.*;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.internal.compiler.ExtraFlags;
import org.junit.Test;

import ch.ethz.inf.dbproject.sqlRevisited.PhysicalTableInterface;
import ch.ethz.inf.dbproject.sqlRevisited.SQLType;
import ch.ethz.inf.dbproject.sqlRevisited.Serializer;
import ch.ethz.inf.dbproject.sqlRevisited.TableSchema;
import ch.ethz.inf.dbproject.sqlRevisited.TableSchemaAttributeDetail;
import ch.ethz.inf.dbproject.sqlRevisited.Codegen.SQLCodegen;
import ch.ethz.inf.dbproject.sqlRevisited.Codegen.SQLOperator;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.SQLLexer;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.SQLParseException;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.SQLParser;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.SQLSemanticException;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.SQLTokenStream;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.SyntaxTreeDynamicNode;
import ch.ethz.inf.dbproject.sqlRevisited.Parser.SyntaxTreeNode;

public class SQLOperatorTest {

	
	String t0 = "select * from User where 5<? and 'f' <= ? and username>'a' and username=password and favoriteNumber=7";
	
	SQLType varchar6 = new SQLType(SQLType.BaseType.Varchar, 6);
	
	TableSchemaAttributeDetail[] UserAttributes = {new TableSchemaAttributeDetail("username", varchar6, true),
												   new TableSchemaAttributeDetail("password", varchar6, false),
												   new TableSchemaAttributeDetail("createdat", SQLType.DATE, false),
												   new TableSchemaAttributeDetail("favoritenumber", SQLType.INTEGER, false)};
	
	TableSchema User = new TableSchema("user", UserAttributes);
	String[] testUsers = {"a", "hans", "cccccc"};
	String[] testPasswords = {"00", "hans", "222222"};
	String[] testDates = {"12-10-12", "12-10-01", "05-01-01"};
	Integer[] testNumber = {0, 7, 11};
	List<TableSchema> schemata = Arrays.asList(User);
	byte[] UserData = new byte[User.getSizeOfEntry()*testUsers.length];
	byte[] ResultData = new byte[User.getSizeOfEntry()*testUsers.length];
	Object[] arguments = {7, "j"};
	
	SQLLexer lex = new SQLLexer();
	SQLParser parser = new SQLParser();
	SQLCodegen codegen = new SQLCodegen();
	
	@Test
	public void test() {
		
		SQLTokenStream tokens;
		try {
			tokens = new SQLTokenStream(lex.tokenize(t0));
			SyntaxTreeDynamicNode parse = parser.parse(tokens);
			SyntaxTreeNode instanciatedTree = parse.dynamicChildren.get(0).instanciateWithSchemata(schemata);//infer schema for all nodes in the AST
			SyntaxTreeNode rewrittenTree = instanciatedTree.rewrite();
			
			//write test data to buffer and create static table on it 
			ByteBuffer sourceBuffer = ByteBuffer.wrap(UserData);
			for (int i=0; i<testUsers.length; i++) {
				Serializer.putBytesFromTuple(User, sourceBuffer, testUsers[i], testPasswords[i], testDates[i], testNumber[i]);
				//System.out.println(Serializer.getVarcharFromByteArray( Arrays.copyOfRange(UserData, i*12, (i+1)*12),0));
			}
			PhysicalTableInterface testTable = new StaticPhysicalTable(User, UserData);
			
			//generate interpreter
			SQLOperator operator = codegen.generateSelectStatement(rewrittenTree, Arrays.asList(testTable), arguments);
			operator.open();
			System.out.println(operator);
			ByteBuffer resultBuffer = ByteBuffer.wrap(ResultData);
			//get results
			int i=0;
			while (operator.hasNext()) {
				operator.getNext(resultBuffer);
				System.out.println("has");
				for (int j=0; j<User.getLength(); j++) {
					if (User.getAttributesTypes()[j].type == SQLType.BaseType.Varchar || User.getAttributesTypes()[j].type == SQLType.BaseType.Date) {
						System.out.println(Serializer.getStringFromByteArray(Arrays.copyOfRange(ResultData, i*User.getSizeOfEntry()+User.getSizeOfAttributes(j), (i+1)*User.getSizeOfEntry())));
					} else {
						System.out.println(Serializer.getIntegerFromByteArray(Arrays.copyOfRange(ResultData, i*User.getSizeOfEntry()+User.getSizeOfAttributes(j), (i+1)*User.getSizeOfEntry())));
					}
				}
				//System.out.println(Serializer.getVarcharFromByteArray(ResultData,0));
				i++;
			}
			
		} catch (SQLParseException e) {
			e.printStackTrace();
			fail("unexpected parse exception.");
			
		} catch (SQLSemanticException e) {
			e.printStackTrace();
			fail("unexpected semantic exception.");

		} catch (Exception e) {
			e.printStackTrace();
			fail("unexpected exception");
		}

	}

}
