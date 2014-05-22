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
		assert(usernamesUser[0].equals(key));
		buf.position(44);
		assert(firstNamesUser[0].equals(Serializer.getStringFromByteBuffer(buf)));
		buf.position(88);
		assert(lastNamesUser[0].equals(Serializer.getStringFromByteBuffer(buf)));
		buf.rewind();
		
		for (int i = 1; i < usernamesUser.length; i++){
			ByteBuffer bufKey = ByteBuffer.allocate(tc.getTableSchema().getSizeOfKeys());
			bufKey.put(Serializer.serializerVarchar(key, tc.getTableSchema().getSizeOfKeys()));
			bufKey.rewind();
			//System.out.println("Key to search : "+Serializer.getStringFromByteBuffer(bufKey));
			bufKey.rewind();
			if (!tc.succ(bufKey, buf))
				System.err.println("ERROR");
			buf.rewind();
			key = Serializer.getStringFromByteBuffer(buf);
			assert(usernamesUser[i].equals(key));
			buf.position(44);
			assert(firstNamesUser[i].equals(Serializer.getStringFromByteBuffer(buf)));
			buf.position(88);
			assert(lastNamesUser[i].equals(Serializer.getStringFromByteBuffer(buf)));
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
		assert(categoryNames[0].equals(key));
		buf.rewind();
		//System.out.println("Key : "+ key);
		for (int i = 1; i < categoryNames.length; i++){
			ByteBuffer bufKey = ByteBuffer.allocate(tc.getTableSchema().getSizeOfKeys());
			bufKey.put(Serializer.serializerVarchar(key, tc.getTableSchema().getSizeOfKeys()));
			bufKey.rewind();
			if (!tc.succ(bufKey, buf))
				System.err.println("ERROR");
			buf.rewind();
			key = Serializer.getStringFromByteBuffer(buf);
			assert(categoryNames[i].equals(key));
			buf.rewind();
			//System.out.println("Key : "+ key);
		}
	}
	
	//Persons
	int[] personIdPerson = new int[]{5, 1};
	String[] firstNamesPerson = new String[]{"Mike", "Georges"};
	String[] lastNamesPerson = new String[]{"Stamford", "Sarans"};
	
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
		assert(personIdPerson[0] == key);
		//System.out.println("Key : "+key);
		buf.position(4);
		assert(firstNamesPerson[0].equals(Serializer.getStringFromByteBuffer(buf)));
		//System.out.println(Serializer.getStringFromByteBuffer(buf));
		buf.position(48);
		assert(lastNamesPerson[0].equals(Serializer.getStringFromByteBuffer(buf)));
		//System.out.println(Serializer.getStringFromByteBuffer(buf));
		buf.rewind();
		
		for (int i = 1; i < personIdPerson.length; i++){
			ByteBuffer bufKey = ByteBuffer.allocate(tc.getTableSchema().getSizeOfKeys());
			bufKey.putInt(key);
			bufKey.rewind();
			bufKey.rewind();
			if (!tc.succ(bufKey, buf))
				System.err.println("ERROR");
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
			buf.put(Serializer.serializerVarchar(authorUsernamePersonNote[i], tc.getTableSchema().getAttributesTypes()[3].byteSizeOfType()));
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
				System.err.println("ERROR");
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
	int[] caseIdCaseDetail = new int[]{10, 8, 9};
	String[] titleCaseDetail = new String[]{"Death of Jeffrey Patterson", "Death of James Philmore", "Death of Beth Davenport"};
	String[] streetCaseDetail = new String[]{null, "Westminsterstreet 5", null};
	String[] cityCaseDetail = new String[]{"London", "London", "London"};
	String[] zipCodeCaseDetail = new String[]{null, null, null};
	boolean[] isOpenCaseDetail = new boolean[]{true, true, true};
	String[] dateCaseDetail = new String[]{"2010-12-10 16-00-00", "2010-11-26 21-00-00", "2010-11-26 23-00-00"};
	String[] descriptionCaseDetail = new String[]{"Sir John found dead in office building. Case of death: poisoning",
			"18 year old James Philmore found dead in a sports centre",
			"Beth Davenport, Junior Minister of Transport found dead at building Site"};
	String[] authorNameCaseDetail = new String[]{"sherlock", "watson", "watson"};
	
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
				System.err.println("ERROR");
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
	String[] textCaseNote = new String[]
			{"Wife: my husband was a pround and happy man. He loved his family and his work. And that he should have taken "+
					"his life in this way is a mistery and a shock to all who knew him"
			};
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
			buf.put(Serializer.serializerVarchar(authorUsernameCaseNote[i], tc.getTableSchema().getAttributesTypes()[3].byteSizeOfType()));
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
				System.err.println("ERROR");
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
		String key2 = Serializer.getStringFromByteBuffer(buf);
		buf.rewind();
		//System.out.println("Key : "+ key1 + " : "+key2);
		for (int i = 1; i < caseIdCategoryForCase.length; i++){
			ByteBuffer bufKey = ByteBuffer.allocate(tc.getTableSchema().getSizeOfKeys());
			bufKey.rewind();
			bufKey.putInt(key1);
			bufKey.rewind();
			if (!tc.succ(bufKey, buf))
				System.err.println("ERROR");
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
				System.err.println("ERROR");
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
				System.err.println("ERROR");
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
				System.err.println("ERROR");
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
				System.err.println("ERROR");
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
	
	
	//If we're motivated, we can include these data above. (Just be careful to not add consistent stuff)

/*
insert into CaseDetail (caseId, title, city, street, zipCode, isOpen, date, description, authorName)
values(3, 'Death of Jennifer Wilson', 'London', 'Gardenstreet 5', 'SW2 5HG', true, '2010-11-28-18-00', 'Jennifer Wilson found dead in a shabby empty apartment at Lauriston Gardens', 'sherlock');

insert into CategoryForCase
    values(3, 'Suicide');

insert into CaseNote (caseId, caseNoteId, text, authorUsername)
    values(3, 0, 'Victim is wearing pink dress, shoes and nailpolish', 'sherlock');

insert into CaseNote (caseId, caseNoteId, text, authorUsername)
    values(3, 1, 'Rache (German for revenge) is etched onto the floor. The nailpolish of the victim is heavily worn off. The victim probably ment to write Rachel, but could not finish in time.', 'sherlock');

insert into CaseNote (caseId, caseNoteId, text, authorUsername)
    values(3, 2, 'The victim lies with the face towards the floor.', 'sherlock');

insert into CaseNote (caseId, caseNoteId, text, authorUsername)
    values(3, 3, 'The victim is left handed. In her mid 30s.', 'sherlock');

insert into CaseNote (caseId, caseNoteId, text, authorUsername)
    values(3, 4, 'The victims wears a marriage ring, which is not clean on the outside, but clean on the inside. The other jewelry she is wearing is speckless. This may indicate that the victim takes off the marriage ring frequently, but does not take care of it. Probably 10 or more years unhappily married, serial adulterer', 'sherlock');

insert into CaseNote (caseId, caseNoteId, text, authorUsername)
    values(3, 5, 'The victims hair is messed up and her blazer is wet. No rain and strong wind in London or vicinity. The victims was just staying for one night in London, before returning home to Cardiff. She probably did not get to the hotel', 'sherlock');

insert into CaseNote (caseId, caseNoteId, text, authorUsername)
    values(3, 6, 'The victims must have carried a pink bag, but it is missing. The murderer must have still got it.', 'sherlock');

insert into CategoryForCase
    values(10, 'Mass Murder');

insert into CategoryForCase
    values(1, 'Mass Murder');

insert into CategoryForCase
    values(2, 'Mass Murder');

insert into CategoryForCase
    values(3, 'Mass Murder');


insert into CaseNote (caseId, caseNoteId, text, authorUsername)
    values(3, 7, 'The phone of the victim is not in the bag. Probably the murderer has her phone.', 'sherlock');

insert into CaseNote (caseId, caseNoteId, text, authorUsername)
values(3, 8, 'The police says Rachel is the  stillborn daughter of the victim. Why would she write her daughters name?', 'sherlock');


insert into CaseNote (caseId, caseNoteId, text, authorUsername)
    values(3, 9, 'Rachel is not a name. Its the password for her email account!', 'sherlock');



insert into Person (personId)
    values(4);

insert into PersonNote (personId, personNoteId, text, authorUsername)
    values(4, 0, 'Husband of the landlady', 'watson');

insert into CaseDetail (caseId, title, city, isOpen, date, authorName)
    values(5, 'Brutal Murder', 'Florida', false, '2005-05-05-00-00', 'sherlock');

insert into CategoryForCase
    values(5, 'Murder');

insert into Conviction
    values(1, 4, 5, '2005-10-10', null);

insert into ConvictionType
    values(1, 'Murder');




insert into Person (personId, firstName, lastName)
    values (1, 'Hank', 'Smith');

insert into PersonNote (personId, personNoteId, text, authorUsername)
    values (1, 0, 'Cab Driver', 'sherlock');

insert into PersonNote (personId, personNoteId, text, authorUsername)
    values (1, 1, 'Middle aged', 'sherlock');

insert into PersonNote (personId, personNoteId, text, authorUsername)
    values (1, 2, 'Two children. Divorced.', 'sherlock');

insert into Suspected
    values (1, 3);

insert into CaseNote (caseId, caseNoteId, text, authorUsername)
    values(3, 10, 'Ok its the taxi driver...and I will just go with him', 'sherlock');

insert into PersonNote (personId, personNoteId, text, authorUsername)
    values (1, 3, 'Terminal illness, desperate, bored, bitter', 'sherlock');

insert into PersonNote (personId, personNoteId, text, authorUsername)
    values (1, 4, 'Was commisioned by someone or something called Moriarty', 'sherlock');

insert into Person (personId, LastName)
    values (2, "Moriarty");

insert into CaseDetail (caseId, title, city, zipCode, isOpen, date, description, authorName)
    values(4, 'Cab Driver is shot', 'London', 'CF10 3AT', true, '2010-11-28-00-00', 'The cab driver got shot from someone standing outside the window at Roland-Kerr Further Education College.', 'watson');

insert into CategoryForCase
    values(4, 'Manslaughter');

insert into CaseNote (caseId, caseNoteId, text, authorUsername)
    values(4, 0, 'Shots fired from a handgun from a large distance. Must have been acclimatised to violence. He only fired when I was in clear danger, so strong moral principles. You are looking for a man with a history of military service, nerves of steel and...', 'sherlock');

insert into Person (personId, firstName, lastName)
    values (3, 'John', 'Watson');

insert into Suspected
    values (2, 4);

insert into CaseNote (caseId, caseNoteId, text, authorUsername)
    values(4, 1, 'Actually, you know what: ignore me. Its just the shock talking', 'sherlock');

insert into CaseNote (caseId, caseNoteId, text, authorUsername)
    values(4, 2, 'He was not a very nice man', 'watson');*/
	
	
}
