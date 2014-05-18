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
		Database db = new Database();
		this.testWriting(db);
		this.testCreationDataStructure(db);
	}
	
	public void testCreationDataStructure(Database db) throws Exception{
		db.getTableConnection("User");
	}
	
	public void testWriting(Database db) throws Exception{
		Serializer serializer = new Serializer();
		TableConnection tc = db.getTableConnection("User");
		SQLType vc40 = new SQLType(SQLType.BaseType.Varchar, 40);
		byte[] username = serializer.getByteArrayFromObject("vinvin", vc40);
		byte[] firstName = serializer.getByteArrayFromObject("Vincent", vc40);
		byte[] lastName = serializer.getByteArrayFromObject("Demotz", vc40);
		byte[] password = serializer.getByteArrayFromObject("Bla", vc40);
		ByteBuffer buf = ByteBuffer.allocate(tc.getTableSchema().getSizeOfEntry());
		System.out.println("Allocate : "+tc.getTableSchema().getSizeOfEntry());
		buf.rewind();
		buf.put(username);
		buf.position(44);
		buf.put(firstName);
		buf.position(88);
		buf.put(lastName);
		buf.position(132);
		buf.put(password);
		System.out.println(buf.toString());
		buf.rewind();
		tc.insert(buf);
		
		byte[] dataRead = new byte[tc.getTableSchema().getSizeOfEntry()];
		if (tc.min(dataRead)){
			System.out.println("That's good beginning");
			System.out.println("With size for first entry "+(int)dataRead[0]+(int)dataRead[1]+(int)dataRead[2]+(int)dataRead[3]);
			System.out.println("Even better, second entry size : "+(int)dataRead[47]);
			System.out.println("Even better, third entry size : "+(int)dataRead[91]);
			System.out.println("Even better, fourth entry size : "+(int)dataRead[135]);
			for (int i = 4; i < dataRead.length; i++){
				System.out.print((char)dataRead[i]);
			}
			System.out.println("\r\nAnd now through serializer : ");
			System.out.println(serializer.getStringFromByteArray(dataRead)+serializer.getStringFromByteArray(dataRead).length());
		} else {
			System.out.println("Failed to read data");
		}
		
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
