package ch.ethz.inf.dbproject.sqlRevisited.test;

import java.nio.ByteBuffer;
import org.junit.Test;
import ch.ethz.inf.dbproject.sqlRevisited.Database;
import ch.ethz.inf.dbproject.sqlRevisited.Serializer;
import ch.ethz.inf.dbproject.sqlRevisited.TableConnection;

public class DatabaseTest {

	@Test
	public void testDatabase() throws Exception{
		Database db = new Database();
		db.dropTables();
		db = new Database();
	}
	
	//Users
	String[] usernamesUser = new String[]{"holmes", "sherlock", "watson"};
	String[] lastNamesUser = new String[]{"Sherlock", "Sherlock", "Watson"};
	String[] firstNamesUser = new String[]{"Holmes", "Holmes", "John"};
	String[] passwordUser = new String[]{"baskervilles", "baskervilles", "mary"};
	
	@Test
	public void testWriteUser() throws Exception{
		Database db = new Database();
		
		//Try to write into User
		TableConnection tc = db.getTableConnection("User");
		for (int i = 0; i < usernamesUser.length; i++){
			ByteBuffer buf = ByteBuffer.allocate(tc.getTableSchema().getSizeOfEntry());
			buf.put(Serializer.serializerVarchar(usernamesUser[i], tc.getTableSchema().getAttributesTypes()[0].byteSizeOfType()));
			buf.put(Serializer.serializerVarchar(lastNamesUser[i], tc.getTableSchema().getAttributesTypes()[1].byteSizeOfType()));
			buf.put(Serializer.serializerVarchar(firstNamesUser[i], tc.getTableSchema().getAttributesTypes()[2].byteSizeOfType()));
			buf.put(Serializer.serializerVarchar(passwordUser[i], tc.getTableSchema().getAttributesTypes()[3].byteSizeOfType()));
			buf.rewind();
			tc.insert(buf);
		}
	}
	
	@Test
	public void testReadUser() throws Exception{
		Database db = new Database();
		
		//Try to read from User
		TableConnection tc = db.getTableConnection("User");
		ByteBuffer buf = ByteBuffer.allocate(tc.getTableSchema().getSizeOfEntry());
		tc.min(buf);
		buf.rewind();
		String key = Serializer.getStringFromByteBuffer(buf);
		//System.out.println("Key : "+key);
		buf.position(44);
		//System.out.println(Serializer.getStringFromByteBuffer(buf));
		buf.position(88);
		//System.out.println(Serializer.getStringFromByteBuffer(buf));
		buf.rewind();
		
		for (int i = 1; i < usernamesUser.length; i++){
			ByteBuffer bufKey = ByteBuffer.allocate(tc.getTableSchema().getSizeOfKeys());
			bufKey.put(Serializer.serializerVarchar(key, tc.getTableSchema().getSizeOfKeys()));
			bufKey.rewind();
			//System.out.println("Key to search : "+Serializer.getStringFromByteBuffer(bufKey));
			bufKey.rewind();
			if (!tc.succ(bufKey, buf))
				System.err.println("ERROR User");
			buf.rewind();
			key = Serializer.getStringFromByteBuffer(buf);
			//System.out.println("Key : "+key);
			buf.position(44);
			//System.out.println(Serializer.getStringFromByteBuffer(buf));
			buf.position(88);
			//System.out.println(Serializer.getStringFromByteBuffer(buf));
		}
	}
	
	//Categories
	String[] categoryNames = new String[]{"Accident", "Suicide", "Murder", "Mass Murder", "Blackmail", "Theft", "Manslaughter"};
	
	@Test
	public void testWriteCategory() throws Exception{
		Database db = new Database();
		
		//Try to write into Category
		TableConnection tc = db.getTableConnection("Category");
		for (int i = 0; i < categoryNames.length; i++){
			ByteBuffer buf = ByteBuffer.allocate(tc.getTableSchema().getSizeOfEntry());
			buf.put(Serializer.serializerVarchar(categoryNames[i], tc.getTableSchema().getAttributesTypes()[0].byteSizeOfType()));
			buf.rewind();
			tc.insert(buf);
		}	
	}
	
	@Test
	public void testReadCategory() throws Exception{
		Database db = new Database();
		
		//Try to read from Category
		TableConnection tc = db.getTableConnection("Category");
		ByteBuffer buf = ByteBuffer.allocate(tc.getTableSchema().getSizeOfEntry());
		tc.min(buf);
		buf.rewind();
		String key = Serializer.getStringFromByteBuffer(buf);
		buf.rewind();
		//System.out.println("Key : "+ key);
		for (int i = 1; i < categoryNames.length; i++){
			ByteBuffer bufKey = ByteBuffer.allocate(tc.getTableSchema().getSizeOfKeys());
			bufKey.put(Serializer.serializerVarchar(key, tc.getTableSchema().getSizeOfKeys()));
			bufKey.rewind();
			if (!tc.succ(bufKey, buf))
				System.err.println("ERROR Category");
			buf.rewind();
			key = Serializer.getStringFromByteBuffer(buf);
			buf.rewind();
			//System.out.println("Key : "+ key);
		}
	}
	
	//Persons
	int[] personIdPerson = new int[]{5, 1};
	String[] firstNamesPerson = new String[]{"Mike", "Georges"};
	String[] lastNamesPerson = new String[]{"Stamford", "Sarans"};
	String[] birthdatesPerson = new String[]{"1969-07-02", "1980-01-05"};
	
	@Test
	public void testWritePerson() throws Exception{
		Database db = new Database();
		
		//Try to write into Person
		TableConnection tc = db.getTableConnection("Person");
		for (int i = 0; i < personIdPerson.length; i++){
			ByteBuffer buf = ByteBuffer.allocate(tc.getTableSchema().getSizeOfEntry());
			buf.putInt(personIdPerson[i]);
			buf.put(Serializer.serializerVarchar(firstNamesPerson[i], tc.getTableSchema().getAttributesTypes()[1].byteSizeOfType()));
			buf.put(Serializer.serializerVarchar(lastNamesPerson[i], tc.getTableSchema().getAttributesTypes()[2].byteSizeOfType()));
			buf.put(Serializer.serializerVarchar(birthdatesPerson[i], tc.getTableSchema().getAttributesTypes()[3].byteSizeOfType()));
			buf.rewind();
			tc.insert(buf);
		}
	}
	
	@Test
	public void testReadPerson() throws Exception{
		Database db = new Database();
		
		//Try to read from Person
		TableConnection tc = db.getTableConnection("Person");
		ByteBuffer buf = ByteBuffer.allocate(tc.getTableSchema().getSizeOfEntry());
		tc.min(buf);
		buf.rewind();
		int key = buf.getInt();
		//System.out.println("Key : "+key);
		buf.position(4);
		//System.out.println(Serializer.getStringFromByteBuffer(buf));
		buf.position(48);
		//System.out.println(Serializer.getStringFromByteBuffer(buf));
		buf.rewind();
		
		for (int i = 1; i < personIdPerson.length; i++){
			ByteBuffer bufKey = ByteBuffer.allocate(tc.getTableSchema().getSizeOfKeys());
			bufKey.putInt(key);
			bufKey.rewind();
			if (!tc.succ(bufKey, buf))
				System.err.println("ERROR Person");
			buf.rewind();
			key = buf.getInt();
			//System.out.println("Key : "+key);
			buf.position(4);
			//System.out.println(Serializer.getStringFromByteBuffer(buf));
			buf.position(48);
			//System.out.println(Serializer.getStringFromByteBuffer(buf));
		}
	}
	
	//PersonsNotes
	int[] personIdPersonNote = new int[]{5, 5, 1};
	int[] personNoteIdPersonNote = new int[]{0, 1, 1};
	String[] textPersonNote = new String[]{"Old friend from school", "Well, never mind", "First note"};
	String[] datePersonNote = new String[]{"2010-01-01 00.00.00","2010-01-01 00.00.00","2010-01-01 00.00.00"};
	String[] authorUsernamePersonNote = new String[]{"watson", "holmes", "xxx"};
	
	@Test
	public void testWritePersonNote() throws Exception{
		Database db = new Database();
		
		//Try to write into PersonNote
		TableConnection tc = db.getTableConnection("PersonNote");
		for (int i = 0; i < personIdPersonNote.length; i++){
			ByteBuffer buf = ByteBuffer.allocate(tc.getTableSchema().getSizeOfEntry());
			buf.putInt(personIdPersonNote[i]);
			buf.putInt(personNoteIdPersonNote[i]);
			buf.put(Serializer.serializerVarchar(textPersonNote[i], tc.getTableSchema().getAttributesTypes()[2].byteSizeOfType()));
			buf.put(Serializer.serializerVarchar(datePersonNote[i], tc.getTableSchema().getAttributesTypes()[3].byteSizeOfType()));
			buf.put(Serializer.serializerVarchar(authorUsernamePersonNote[i], tc.getTableSchema().getAttributesTypes()[4].byteSizeOfType()));
			buf.rewind();
			tc.insert(buf);
		}
	}
	
	@Test
	public void testReadPersonNote() throws Exception{
		Database db = new Database();
		
		//Try to read from PersonNote
		TableConnection tc = db.getTableConnection("PersonNote");
		ByteBuffer buf = ByteBuffer.allocate(tc.getTableSchema().getSizeOfEntry());
		tc.min(buf);
		buf.rewind();
		int key1 = buf.getInt();
		int key2 = buf.getInt();
		//assert(personIdPerson[0] == key);
		//System.out.println("Keys : "+key1 + " "+key2);
		buf.position(8);
		//assert(firstNamesPerson[0].equals(Serializer.getStringFromByteBuffer(buf)));
		//System.out.println(Serializer.getStringFromByteBuffer(buf));
		buf.position(8+tc.getTableSchema().getAttributesTypes()[2].byteSizeOfType());
		//assert(lastNamesPerson[0].equals(Serializer.getStringFromByteBuffer(buf)));
		//System.out.println(Serializer.getStringFromByteBuffer(buf));
		buf.rewind();
		
		for (int i = 1; i < personIdPersonNote.length; i++){
			ByteBuffer bufKey = ByteBuffer.allocate(tc.getTableSchema().getSizeOfKeys());
			bufKey.putInt(key1);
			bufKey.putInt(key2);
			bufKey.rewind();
			if (!tc.succ(bufKey, buf))
				System.err.println("ERROR PersonNote");
			buf.rewind();
			key1 = buf.getInt();
			key2 = buf.getInt();
			//System.out.println("Keys : "+key1 + " "+key2);
			buf.position(8);
			//System.out.println(Serializer.getStringFromByteBuffer(buf));
			buf.position(8+tc.getTableSchema().getAttributesTypes()[2].byteSizeOfType());
			//System.out.println(Serializer.getStringFromByteBuffer(buf));
		}
	}

	//CaseDetail
	int[] caseIdCaseDetail = new int[]{10, 8, 9, 3, 5, 4};
	String[] titleCaseDetail = new String[]{"Death of Jeffrey Patterson", "Death of James Philmore", "Death of Beth Davenport", "Death of Jennifer Wilson", "Brutal Murder", "Cab Driver is shot"};
	String[] streetCaseDetail = new String[]{null, "Westminsterstreet 5", null, "Gardenstreet 5", null, null};
	String[] cityCaseDetail = new String[]{"London", "London", "London", "London", "Florida", "London"};
	String[] zipCodeCaseDetail = new String[]{null, null, null, "SW2 5HG", null, "CF10 3AT"};
	boolean[] isOpenCaseDetail = new boolean[]{true, true, true, true, false, true};
	String[] dateCaseDetail = new String[]{"2010-12-10 16.00.00", "2010-11-26 21.00.00", "2010-11-26 23.00.00", "2010-11-28 18.00.00", "2005-05-05 00.00.00", "2010-11-28 00.00.00"};
	String[] descriptionCaseDetail = new String[]{"Sir John found dead in office building. Case of death: poisoning",
			"18 year old James Philmore found dead in a sports centre",
			"Beth Davenport, Junior Minister of Transport found dead at building Site",
			"Jennifer Wilson found dead in a shabby empty apartment at Lauriston Gardens",
			null,
			"The cab driver got shot from someone standing outside the window at Roland-Kerr Further Education College."};
	String[] authorNameCaseDetail = new String[]{"sherlock", "watson", "watson", "sherlock", "sherlock", "watson"};
	
	@Test
	public void testWriteCaseDetai() throws Exception{
		Database db = new Database();
		
		//Try to write into CaseDetail
		TableConnection tc = db.getTableConnection("CaseDetail");
		for (int i = 0; i < caseIdCaseDetail.length; i++){
			ByteBuffer buf = ByteBuffer.allocate(tc.getTableSchema().getSizeOfEntry());
			buf.putInt(caseIdCaseDetail[i]);
			buf.put(Serializer.serializerVarchar(titleCaseDetail[i], tc.getTableSchema().getAttributesTypes()[1].byteSizeOfType()));
			buf.put(Serializer.serializerVarchar(streetCaseDetail[i], tc.getTableSchema().getAttributesTypes()[2].byteSizeOfType()));
			buf.put(Serializer.serializerVarchar(cityCaseDetail[i], tc.getTableSchema().getAttributesTypes()[3].byteSizeOfType()));
			buf.put(Serializer.serializerVarchar(zipCodeCaseDetail[i], tc.getTableSchema().getAttributesTypes()[4].byteSizeOfType()));
			buf.put(Serializer.serializerBoolean(isOpenCaseDetail[i]));
			buf.put(Serializer.serializerVarchar(dateCaseDetail[i], tc.getTableSchema().getAttributesTypes()[6].byteSizeOfType()));
			buf.put(Serializer.serializerVarchar(descriptionCaseDetail[i], tc.getTableSchema().getAttributesTypes()[7].byteSizeOfType()));
			buf.put(Serializer.serializerVarchar(authorNameCaseDetail[i], tc.getTableSchema().getAttributesTypes()[8].byteSizeOfType()));
			buf.rewind();
			tc.insert(buf);
		}
	}
	
	@Test
	public void testReadCaseDetail() throws Exception{
		Database db = new Database();
		
		//Try to read from PersonNote
		TableConnection tc = db.getTableConnection("CaseDetail");
		ByteBuffer buf = ByteBuffer.allocate(tc.getTableSchema().getSizeOfEntry());
		tc.min(buf);
		buf.rewind();
		int key = buf.getInt();
		//System.out.println("Key : "+key);
		buf.position(4);
		//System.out.println(Serializer.getStringFromByteBuffer(buf));
		
		//assert(firstNamesPerson[0].equals(Serializer.getStringFromByteBuffer(buf)));
		int pos = 4+tc.getTableSchema().getAttributesTypes()[1].byteSizeOfType();
		buf.position(pos);
		//System.out.println(Serializer.getStringFromByteBuffer(buf));
		pos += tc.getTableSchema().getAttributesTypes()[2].byteSizeOfType();
		buf.position(pos);
		//System.out.println(Serializer.getStringFromByteBuffer(buf));
		pos += tc.getTableSchema().getAttributesTypes()[3].byteSizeOfType();
		buf.position(pos);
		//System.out.println(Serializer.getStringFromByteBuffer(buf));
		pos += tc.getTableSchema().getAttributesTypes()[4].byteSizeOfType();
		buf.position(pos);
		//System.out.println(Serializer.getBooleanFromByteBuffer(buf));
		pos += tc.getTableSchema().getAttributesTypes()[5].byteSizeOfType();
		buf.position(pos);
		//System.out.println(Serializer.getStringFromByteBuffer(buf));
		pos += tc.getTableSchema().getAttributesTypes()[6].byteSizeOfType();
		buf.position(pos);
		//System.out.println(Serializer.getStringFromByteBuffer(buf));
		pos += tc.getTableSchema().getAttributesTypes()[7].byteSizeOfType();
		buf.position(pos);
		//System.out.println(Serializer.getStringFromByteBuffer(buf));
		pos += tc.getTableSchema().getAttributesTypes()[8].byteSizeOfType();
		buf.rewind();
		
		for (int i = 1; i < caseIdCaseDetail.length; i++){
			ByteBuffer bufKey = ByteBuffer.allocate(tc.getTableSchema().getSizeOfKeys());
			bufKey.putInt(key);
			bufKey.rewind();
			if (!tc.succ(bufKey, buf))
				System.err.println("ERROR casedetail");
			buf.rewind();
			key = buf.getInt();
			//System.out.println("Key : "+key);
			buf.position(4);
			//System.out.println(Serializer.getStringFromByteBuffer(buf));
			
			//assert(firstNamesPerson[0].equals(Serializer.getStringFromByteBuffer(buf)));
			pos = 4+tc.getTableSchema().getAttributesTypes()[1].byteSizeOfType();
			buf.position(pos);
			//System.out.println(Serializer.getStringFromByteBuffer(buf));
			pos += tc.getTableSchema().getAttributesTypes()[2].byteSizeOfType();
			buf.position(pos);
			//System.out.println(Serializer.getStringFromByteBuffer(buf));
			pos += tc.getTableSchema().getAttributesTypes()[3].byteSizeOfType();
			buf.position(pos);
			//System.out.println(Serializer.getStringFromByteBuffer(buf));
			pos += tc.getTableSchema().getAttributesTypes()[4].byteSizeOfType();
			buf.position(pos);
			//System.out.println(Serializer.getBooleanFromByteBuffer(buf));
			pos += tc.getTableSchema().getAttributesTypes()[5].byteSizeOfType();
			buf.position(pos);
			//System.out.println(Serializer.getStringFromByteBuffer(buf));
			pos += tc.getTableSchema().getAttributesTypes()[6].byteSizeOfType();
			buf.position(pos);
			//System.out.println(Serializer.getStringFromByteBuffer(buf));
			pos += tc.getTableSchema().getAttributesTypes()[7].byteSizeOfType();
			buf.position(pos);
			//System.out.println(Serializer.getStringFromByteBuffer(buf));
			pos += tc.getTableSchema().getAttributesTypes()[8].byteSizeOfType();
			buf.rewind();
		}
	}
	

	//CaseNote
	int[] caseIdCaseNote = new int[]{10};
	int[] caseNoteIdCaseNote = new int[]{0};
	/*String[] textCaseNote = new String[]
			{"Wife: my husband was a pround and happy man. He loved his family and his work. And that he should have taken "+
					"his life in this way is a mistery and a shock to all who knew him"
			};*/
	String[] textCaseNote = new String[]{"Comment"};
	String[] dateCaseNote = new String[]{"2010-12-12 00.00.00"};
	String[] authorUsernameCaseNote = new String[]{"watson"};
	
	@Test
	public void testWriteCaseNote() throws Exception{
		Database db = new Database();
		
		//Try to write into CaseNote
		TableConnection tc = db.getTableConnection("CaseNote");
		for (int i = 0; i < caseIdCaseNote.length; i++){
			ByteBuffer buf = ByteBuffer.allocate(tc.getTableSchema().getSizeOfEntry());
			buf.putInt(caseIdCaseNote[i]);
			buf.putInt(caseNoteIdCaseNote[i]);
			buf.put(Serializer.serializerVarchar(textCaseNote[i], tc.getTableSchema().getAttributesTypes()[2].byteSizeOfType()));
			buf.put(Serializer.serializerVarchar(dateCaseNote[i], tc.getTableSchema().getAttributesTypes()[3].byteSizeOfType()));
			buf.put(Serializer.serializerVarchar(authorUsernameCaseNote[i], tc.getTableSchema().getAttributesTypes()[4].byteSizeOfType()));
			buf.rewind();
			tc.insert(buf);
		}
	}
	
	@Test
	public void testReadCaseNote() throws Exception{
		Database db = new Database();
		
		//Try to read from CaseNote
		TableConnection tc = db.getTableConnection("CaseNote");
		ByteBuffer buf = ByteBuffer.allocate(tc.getTableSchema().getSizeOfEntry());
		tc.min(buf);
		buf.rewind();
		int key1 = buf.getInt();
		int key2 = buf.getInt();
		//assert(caseIdCase[0] == key);
		//System.out.println("Keys : "+key1 + " "+key2);
		buf.position(8);
		//System.out.println(Serializer.getStringFromByteBuffer(buf));
		buf.position(8+tc.getTableSchema().getAttributesTypes()[2].byteSizeOfType());
		//System.out.println(Serializer.getStringFromByteBuffer(buf));
		buf.rewind();
		
		for (int i = 1; i < caseIdCaseNote.length; i++){
			ByteBuffer bufKey = ByteBuffer.allocate(tc.getTableSchema().getSizeOfKeys());
			bufKey.putInt(key1);
			bufKey.putInt(key2);
			bufKey.rewind();
			if (!tc.succ(bufKey, buf))
				System.err.println("ERROR casenote");
			buf.rewind();
			key1 = buf.getInt();
			key2 = buf.getInt();
			//System.out.println("Keys : "+key1 + " "+key2);
			buf.position(8);
			//System.out.println(Serializer.getStringFromByteBuffer(buf));
			buf.position(8+tc.getTableSchema().getAttributesTypes()[2].byteSizeOfType());
			//System.out.println(Serializer.getStringFromByteBuffer(buf));
		}
	}
	
	//CategoryForCase
	int[] caseIdCategoryForCase = new int[]{10, 1, 2};
	String[] categoryNameCategoryForCase = new String[]{"Suicide", "Suicide", "Suicide"};
	
	@Test
	public void testWriteCategoryForCase() throws Exception{
		Database db = new Database();
		
		//Try to write into CategoryForCase
		TableConnection tc = db.getTableConnection("CategoryForCase");
		for (int i = 0; i < caseIdCategoryForCase.length; i++){
			ByteBuffer buf = ByteBuffer.allocate(tc.getTableSchema().getSizeOfEntry());
			buf.putInt(caseIdCategoryForCase[i]);
			buf.put(Serializer.serializerVarchar(categoryNameCategoryForCase[i], tc.getTableSchema().getAttributesTypes()[1].byteSizeOfType()));
			buf.rewind();
			if (!tc.insert(buf))
				System.err.println("ERROR");
		}	
	}
	
	@Test
	public void testReadCategoryForCase() throws Exception{
		Database db = new Database();
		
		//Try to read from CategoryForCase
		TableConnection tc = db.getTableConnection("CategoryForCase");
		ByteBuffer buf = ByteBuffer.allocate(tc.getTableSchema().getSizeOfEntry());
		tc.min(buf);
		buf.rewind();
		int key1 = buf.getInt();
		@SuppressWarnings("unused")
		String key2 = Serializer.getStringFromByteBuffer(buf);
		buf.rewind();
		//System.out.println("Key : "+ key1 + " : "+key2);
		for (int i = 1; i < caseIdCategoryForCase.length; i++){
			ByteBuffer bufKey = ByteBuffer.allocate(tc.getTableSchema().getSizeOfKeys());
			bufKey.rewind();
			bufKey.putInt(key1);
			bufKey.put(Serializer.serializerVarchar(key2, tc.getTableSchema().getAttributesTypes()[1].byteSizeOfType()));
			bufKey.rewind();
			if (!tc.succ(bufKey, buf))
				System.err.println("ERROR categoryforcase");
			buf.rewind();
			key1 = buf.getInt();
			key2 = Serializer.getStringFromByteBuffer(buf);
			buf.rewind();
			//System.out.println("Key : "+ key1 +" : "+key2);
		}
	}
	
	//Suspected
	int[] personIdSuspected = new int[]{5, 1};
	int[] caseIdSuspected = new int[]{9, 9};
	
	@Test
	public void testWriteSuspected() throws Exception{
		Database db = new Database();
		
		//Try to write into Suspected
		TableConnection tc = db.getTableConnection("Suspected");
		for (int i = 0; i < personIdSuspected.length; i++){
			ByteBuffer buf = ByteBuffer.allocate(tc.getTableSchema().getSizeOfEntry());
			buf.putInt(personIdSuspected[i]);
			buf.putInt(caseIdSuspected[i]);
			buf.rewind();
			if (!tc.insert(buf))
				System.err.println("ERROR suspected write");
		}	
	}
	
	@Test
	public void testReadSuspected() throws Exception{
		Database db = new Database();
		
		//Try to read from Suspected
		TableConnection tc = db.getTableConnection("Suspected");
		ByteBuffer buf = ByteBuffer.allocate(tc.getTableSchema().getSizeOfEntry());
		tc.min(buf);
		buf.rewind();
		int key1 = buf.getInt();
		int key2 = buf.getInt();
		buf.rewind();
		//System.out.println("Key : "+ key1 + " : "+key2);
		for (int i = 1; i < personIdSuspected.length; i++){
			ByteBuffer bufKey = ByteBuffer.allocate(tc.getTableSchema().getSizeOfKeys());
			bufKey.rewind();
			bufKey.putInt(key1);
			bufKey.putInt(key2);
			bufKey.rewind();
			if (!tc.succ(bufKey, buf))
				System.err.println("ERROR suspected read");
			buf.rewind();
			key1 = buf.getInt();
			key2 = buf.getInt();
			buf.rewind();
			//System.out.println("Key : "+ key1 +" : "+key2);
		}
	}
	
	//Conviction
	int[] convictionIdConviction = new int[]{1, 2};
	int[] personIdConviction = new int[]{5, 1};
	int[] caseIdConviction = new int[]{10, 10};
	String[] startDateConviction = new String[]{"2011-12-12", "2009-02-07"};
	String[] endDateConviction = new String[]{"2012-12-12", "2010-02-07"};
	
	
	@Test
	public void testWriteConviction() throws Exception{
		Database db = new Database();
		
		//Try to write into Suspected
		TableConnection tc = db.getTableConnection("Conviction");
		for (int i = 0; i < personIdSuspected.length; i++){
			ByteBuffer buf = ByteBuffer.allocate(tc.getTableSchema().getSizeOfEntry());
			buf.rewind();
			buf.putInt(convictionIdConviction[i]);
			buf.putInt(personIdConviction[i]);
			buf.putInt(caseIdConviction[i]);
			buf.put(Serializer.serializerVarchar(startDateConviction[i], tc.getTableSchema().getAttributesTypes()[3].byteSizeOfType()));
			buf.put(Serializer.serializerVarchar(endDateConviction[i], tc.getTableSchema().getAttributesTypes()[4].byteSizeOfType()));
			buf.rewind();
			if (!tc.insert(buf))
				System.err.println("ERROR conviction write");
		}	
	}
	
	@Test
	public void testReadConviction() throws Exception{
		Database db = new Database();
		
		//Try to read from Suspected
		TableConnection tc = db.getTableConnection("Conviction");
		ByteBuffer buf = ByteBuffer.allocate(tc.getTableSchema().getSizeOfEntry());
		tc.min(buf);
		buf.rewind();
		int key1 = buf.getInt();
		//System.out.println("Key : "+ key1);
		//System.out.println(buf.getInt());
		//System.out.println(buf.getInt());
		//int pos = buf.position();
		//System.out.println(Serializer.getStringFromByteBuffer(buf));
		//pos += tc.getTableSchema().getAttributesTypes()[4].byteSizeOfType();
		//buf.position(pos);
		//System.out.println(Serializer.getStringFromByteBuffer(buf));
		buf.rewind();
		for (int i = 1; i < personIdConviction.length; i++){
			ByteBuffer bufKey = ByteBuffer.allocate(tc.getTableSchema().getSizeOfKeys());
			bufKey.rewind();
			bufKey.putInt(key1);
			bufKey.rewind();
			if (!tc.succ(bufKey, buf))
				System.err.println("ERROR conviction read");
			buf.rewind();
			key1 = buf.getInt();
			//System.out.println("Key : "+ key1);
			//System.out.println(buf.getInt());
			//System.out.println(buf.getInt());
			//pos = buf.position();
			//System.out.println(Serializer.getStringFromByteBuffer(buf));
			//pos += tc.getTableSchema().getAttributesTypes()[4].byteSizeOfType();
			//buf.position(pos);
			//System.out.println(Serializer.getStringFromByteBuffer(buf));
			buf.rewind();
		}
	}
}
