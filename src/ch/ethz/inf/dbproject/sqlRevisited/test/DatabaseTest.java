package ch.ethz.inf.dbproject.sqlRevisited.test;

import java.nio.ByteBuffer;

import org.junit.Test;

import ch.ethz.inf.dbproject.sqlRevisited.Connection;
import ch.ethz.inf.dbproject.sqlRevisited.Database;
import ch.ethz.inf.dbproject.sqlRevisited.SQLType;
import ch.ethz.inf.dbproject.sqlRevisited.Serializer;
import ch.ethz.inf.dbproject.sqlRevisited.TableConnection;
import ch.ethz.inf.dbproject.sqlRevisited.TableSchema;

public class DatabaseTest {

	@Test
	public void testDatabase() throws Exception{
		//this.testMetaDataCreation();
		this.testWriting();
	}
	
	
	
	@Test
	public void testWriting() throws Exception{
		Serializer serializer = new Serializer();
		Database db = new Database();
		TableConnection tc = db.getTableConnection("User");
		SQLType vc40 = new SQLType(SQLType.BaseType.Varchar, 40);
		byte[] username = serializer.getByteArrayFromObject("vinvin", vc40);
		byte[] firstName = serializer.getByteArrayFromObject("Vincent", vc40);
		byte[] lastName = serializer.getByteArrayFromObject("Demotz", vc40);
		byte[] password = serializer.getByteArrayFromObject("Bla", vc40);
		ByteBuffer buf = ByteBuffer.allocate(vc40.byteSizeOfType());
		buf.rewind();
		buf.put(username);
		buf.put(firstName);
		buf.put(lastName);
		buf.put(password);
		System.out.println(buf.toString());
		buf.rewind();
		tc.insert(buf);
	}
	
	@Test
	public void testMetaDataCreation() throws Exception{
		Database db = new Database();
		TableSchema tsUser = null;
		TableSchema tsCaseDetail = null;
		TableSchema tsCaseNote = null;
		TableSchema tsCategory = null;
		TableSchema tsConviction = null;
		TableSchema tsBla = null;
		try{
			tsUser = db.getTableSchema("User");
			tsCaseDetail = db.getTableSchema("CaseDetail");
			tsCaseNote = db.getTableSchema("CaseNote");
			tsCategory = db.getTableSchema("Category");
			tsConviction = db.getTableSchema("Conviction");
		} catch (Exception ex){
			ex.printStackTrace();
		}

		try{
			db.getTableSchema("Bla");
		} catch (Exception exFailed){
			exFailed.printStackTrace();
		}
		
		assert(tsCaseDetail != null && tsCaseNote != null && tsCategory != null && tsConviction != null && tsBla == null);
		assert(tsCaseDetail.getAttributesNames()[0].equals("caseId"));
		
		System.out.println("The table is called : "+tsUser.getTableName());
		System.out.println("It has attributes : ");
		for (int i = 0; i < tsUser.getAttributesNames().length; i++){
			System.out.print("The attribute " + tsUser.getAttributesNames()[i]+" has type : ");
			System.out.print(tsUser.getAttributesTypes()[i].type.toString()+"("+tsUser.getAttributesTypes()[i].size.toString()+")"+", ");
			System.out.print("primary key : " +tsUser.isPrimaryKey(i)+"\r\n");
		}
	}
}
