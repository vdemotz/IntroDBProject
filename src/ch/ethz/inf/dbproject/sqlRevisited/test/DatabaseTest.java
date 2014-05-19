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
		this.testDelete(db);
		//this.testWriting(db);
		//this.testCreationDataStructure(db);
	}
	
	public void testDelete(Database db) throws Exception{
		TableConnection tc = db.getTableConnection("User");
		SQLType vc40 = new SQLType(SQLType.BaseType.Varchar, 40);
		byte[] usernameKey = Serializer.getByteArrayFromObject("fb", vc40);
		ByteBuffer bufKey = ByteBuffer.allocate(tc.getTableSchema().getSizeOfKeys());
		bufKey.put(usernameKey);
		bufKey.rewind();
		tc.delete(bufKey);
	}
	
	public void testWriting2(Database db) throws Exception{
		Serializer serializer = new Serializer();
		SQLType i = new SQLType(SQLType.BaseType.Integer);
		SQLType vc40 = new SQLType(SQLType.BaseType.Varchar, 40);
		SQLType date = new SQLType(SQLType.BaseType.Date);
		TableConnection tc = db.getTableConnection("Person");
		byte[] personId = Serializer.getByteArrayFromObject(1, i);
		byte[] firstName = Serializer.getByteArrayFromObject("Alphonse", vc40);
		byte[] lastName = Serializer.getByteArrayFromObject("Roger", vc40);
		byte[] birthdate = Serializer.getByteArrayFromObject("12-12-2002", date);
		ByteBuffer buf = ByteBuffer.allocate(tc.getTableSchema().getSizeOfEntry());
		buf.rewind();
		buf.put(personId);
		buf.position(i.byteSizeOfType());
		System.out.println(buf.position());
		buf.put(firstName);
		buf.position(vc40.byteSizeOfType()+i.byteSizeOfType());
		System.out.println(buf.position());
		buf.put(lastName);
		System.out.println(buf.position());

		buf.position(vc40.byteSizeOfType()+i.byteSizeOfType()+vc40.byteSizeOfType());
		System.out.println(buf.position());
		System.out.println(buf.remaining()+" "+birthdate.length);

		buf.put(birthdate);
		buf.rewind();
		//tc.insert(buf);
		
		byte[] usernameKey = Serializer.getByteArrayFromObject(1, i);
		ByteBuffer bufKey = ByteBuffer.allocate(tc.getTableSchema().getSizeOfKeys());
		bufKey.put(usernameKey);
		
		byte[] dataRead = new byte[tc.getTableSchema().getSizeOfEntry()];
		/*if (tc.min(dataRead)){
			System.out.println("Not so bad");
			for (int ith = 4; ith < dataRead.length; ith++){
				System.out.print((char)dataRead[ith]+ " "+ith);
			}
		}*/
	}
	
	public void testCreationDataStructure(Database db) throws Exception{
		db.getTableConnection("User");
	}
	
	public void testWriting(Database db) throws Exception{
		Serializer serializer = new Serializer();
		TableConnection tc = db.getTableConnection("User");
		SQLType vc40 = new SQLType(SQLType.BaseType.Varchar, 40);
		byte[] username = Serializer.getByteArrayFromObject("fbssda", vc40);
		byte[] firstName = Serializer.getByteArrayFromObject("Vincent", vc40);
		byte[] lastName = Serializer.getByteArrayFromObject("Demotz", vc40);
		byte[] password = Serializer.getByteArrayFromObject("Bla", vc40);
		ByteBuffer buf = ByteBuffer.allocate(tc.getTableSchema().getSizeOfEntry());
		buf.rewind();
		buf.put(username);
		buf.position(44);
		buf.put(firstName);
		buf.position(88);
		buf.put(lastName);
		buf.position(132);
		buf.put(password);
		//System.out.println(buf.toString());
		buf.rewind();
		//tc.insert(buf);
		
		byte[] usernameKey = Serializer.getByteArrayFromObject("fb", vc40);
		ByteBuffer bufKey = ByteBuffer.allocate(tc.getTableSchema().getSizeOfKeys());
		bufKey.put(usernameKey);
		bufKey.rewind();
		
		ByteBuffer dataRead = ByteBuffer.allocate(tc.getTableSchema().getSizeOfEntry());
		byte[] dataReadSucc = new byte[tc.getTableSchema().getSizeOfEntry()];
		if (tc.get(bufKey, dataRead)){
			System.out.println("Read data");
			/*System.out.println("That's good beginning");
			System.out.println("With size for first entry "+(int)dataRead[0]+(int)dataRead[1]+(int)dataRead[2]+(int)dataRead[3]);
			System.out.println("Even better, second entry size : "+(int)dataRead[47]);
			System.out.println("Even better, third entry size : "+(int)dataRead[91]);
			System.out.println("Even better, fourth entry size : "+(int)dataRead[135]);*/
			/*for (int i = 4; i < dataRead.length; i++){
			System.out.print((char)dataRead[i]);
		}*/
			//dataRead.rewind();
			/*while (dataRead.hasRemaining())
				System.out.print((char)dataRead.get()+" "+1);
			dataRead.rewind();
			System.out.println("\r\nAnd now through serializer : ");
			System.out.println(serializer.getStringFromByteBuffer(dataRead)+" "+serializer.getStringFromByteBuffer(dataRead).length());
			ByteBuffer key = ByteBuffer.wrap(usernameKey);
			key.rewind();
			ByteBuffer toRead = ByteBuffer.wrap(dataReadSucc);
			toRead.rewind();
			/*tc.succ(key, toRead);
			System.out.println("\r\nAnd now through serializer : ");
			System.out.println(Serializer.getStringFromByteArray(dataReadSucc)+" "+Serializer.getStringFromByteArray(dataReadSucc).length());
			 */
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
