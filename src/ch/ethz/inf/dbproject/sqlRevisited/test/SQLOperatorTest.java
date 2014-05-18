package ch.ethz.inf.dbproject.sqlRevisited.test;

import static org.junit.Assert.*;

import org.junit.Test;

import ch.ethz.inf.dbproject.sqlRevisited.SQLType;
import ch.ethz.inf.dbproject.sqlRevisited.TableSchema;
import ch.ethz.inf.dbproject.sqlRevisited.TableSchemaAttributeDetail;

public class SQLOperatorTest {

	
	String t0 = "select * from User where username='linus'";
	
	TableSchemaAttributeDetail[] UserAttributes = {new TableSchemaAttributeDetail("username", new SQLType(SQLType.BaseType.Varchar, 40), true)};
	TableSchema User = new TableSchema("User", UserAttributes);
	
	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
