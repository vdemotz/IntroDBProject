package ch.ethz.inf.dbproject.sqlRevisited.test;

import static org.junit.Assert.*;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.internal.compiler.ExtraFlags;
import org.junit.Test;

import ch.ethz.inf.dbproject.sqlRevisited.PhysicalTableInterface;
import ch.ethz.inf.dbproject.sqlRevisited.SQLType;
import ch.ethz.inf.dbproject.sqlRevisited.Serializer;
import ch.ethz.inf.dbproject.sqlRevisited.TableSchema;
import ch.ethz.inf.dbproject.sqlRevisited.TableSchemaAttributeDetail;
import ch.ethz.inf.dbproject.sqlRevisited.TableSet;
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

	SQLLexer lex = new SQLLexer();
	SQLParser parser = new SQLParser();
	SQLCodegen codegen = new SQLCodegen();
	
	TableSet tableDefinitions = new TableSet();

	String[] UserUsername = {"sherlock", "watson", "linus", "abraxas"};
	String[] UserPassword = {"baskervilles", "molly", "cat123", "password007"};
	String[] UserFirstname = {"Sherlock", "John", "Torwalds", "Abraxas"};
	String[] UserLastname = {"Holmes", "Watson", "Linus", "Xeniferos"};
	
	
	Integer[] CaseDetailCaseId = {0, 1};
	String[] CaseDetailTitle = {"Study in Pink", "Hounds of Baskervilles"};
	String[] CaseDetailCity = {"London", "London"};
	String[] CaseDetailStreet = {"Gardenstreet 4", "Westmisterstreet 14"};
	String[] CaseDetailZipCode = {"8042 D", "42990 CD"};
	Boolean[] CaseDetailIsOpen = {true, false};
	String[] CaseDetailDate = {"2001-11-12", "2001-11-12"};
	String[] CaseDetailDescription = {"Mysterious murder.", "Fascinating Incident."};
	String[] CaseDetailAuthorName = {"sherlock", "watson"};
	
	
	String query0 = "select * from User where 5<? and 'f' <= 'f' and username=?";
	String query1 = "select * from CaseDetail where caseId=0";
	String query2 = "select * from User, CaseDetail where caseId<10";
	String query3 = "select * from User ua, User ub, User uc where ua.username=ub.username and ub.username=uc.username";
	String query4 = "select * from User order by username desc";
	String query5 = "select * from User order by username asc";
	String query6 = "select * from CaseDetail order by date desc, caseId asc";
	String query7 = "select * from CaseDetail where isOpen=true and isOpen=?";
	String query8 = "select CaseDetail.*, firstname, User.lastname from CaseDetail,User where CaseDetail.authorName=username and CaseDetail.authorName=?";
	String query9 = "select * from  CaseDetail a, CaseDetail b, CaseDetail c group by c.caseId";
	String query10 = "select * from User where username='linus' group by username";
	String query11 = "select * from User group by firstName";
	String query12 = "select b.*, count(*) from User a, User b group by a.username";
	String query13 = "select max(lastname) from User a";
	String query14 = "select distinct a.* from User a, User b, User c, User d";
	String query15 = "select * from (select firstname, lastname from User) as S";
	
	Object[] query0Args = {7, "sherlock"};
	

	String[] queries = {query0, query1, query2, query3, query4, query5, query6, query7, query8, query9, query10, query11, query12, query13, query14, query15};
	Object[][] queryArgs = {query0Args, {}, {}, {}, {}, {}, {}, {true}, {"watson"}, {}, {}, {}, {}, {}, {}, {}};

	byte[] UserData;
	byte[] CaseDetailData;
	
	@Test
	public void test() {
		
		SQLTokenStream tokens;
		
		TableSchema User = tableDefinitions.getSchemaForName("user");
		TableSchema CaseDetail = tableDefinitions.getSchemaForName("casedetail");
		
		UserData = new byte[UserUsername.length*User.getSizeOfEntry()];
		CaseDetailData = new byte[CaseDetailCaseId.length*CaseDetail.getSizeOfEntry()];
		
		byte[] ResultData = new byte[UserData.length*CaseDetailData.length*100];
		
		try {
			//write test data to buffer and create static table on it
			ByteBuffer sourceBuffer = ByteBuffer.wrap(UserData);
			for (int i=0; i<UserUsername.length; i++) {
				Serializer.putBytesFromTuple(User, sourceBuffer, UserUsername[i], UserFirstname[i], UserLastname[i], UserPassword[i]);
			}
			PhysicalTableInterface UserTable = new StaticPhysicalTable(User, UserData);
			
			sourceBuffer = ByteBuffer.wrap(CaseDetailData);
			for (int i=0; i<CaseDetailCaseId.length; i++) {
				Serializer.putBytesFromTuple(CaseDetail, sourceBuffer, CaseDetailCaseId[i], CaseDetailTitle[i], CaseDetailStreet[i], CaseDetailCity[i], CaseDetailZipCode[i], CaseDetailIsOpen[i],
											CaseDetailDate[i], CaseDetailDescription[i], CaseDetailAuthorName[i]);
			}
			PhysicalTableInterface CaseDetailTable = new StaticPhysicalTable(CaseDetail, CaseDetailData);
			
			List<PhysicalTableInterface> tables = Arrays.asList(UserTable, CaseDetailTable);
			List<TableSchema> schemata = Arrays.asList(User, CaseDetail);
			
			for (int curQuery = 0; curQuery < queries.length; curQuery++) {
					
					tokens = new SQLTokenStream(lex.tokenize(queries[curQuery]));
					
					SyntaxTreeDynamicNode parse = parser.parse(tokens).getSyntaxTreeDynamicNode();
					SyntaxTreeNode instanciatedTree = parse.dynamicChildren.get(0).instanciateWithSchemata(schemata);//infer schema for all nodes in the AST
					
					SyntaxTreeNode rewrittenTree = instanciatedTree.rewrite();
					
					//generate interpreter
					SQLOperator operator = codegen.generateSelectStatement(rewrittenTree, tables, queryArgs[curQuery]);
					operator.open();
					System.out.println(operator);
					System.out.println(rewrittenTree.schema);
					ByteBuffer resultBuffer = ByteBuffer.wrap(ResultData);
					//get results
					int i=0;
					while (operator.next(resultBuffer)) {
						Object[] result = Serializer.getObjectsFromBytes(Arrays.copyOfRange(ResultData, i*operator.schema.getSizeOfEntry(), (i+1)*operator.schema.getSizeOfEntry()), operator.schema);
						System.out.println(Arrays.deepToString(result));
						i++;
					}
					System.out.println();
					
					
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
