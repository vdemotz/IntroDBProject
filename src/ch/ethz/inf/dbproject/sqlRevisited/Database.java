package ch.ethz.inf.dbproject.sqlRevisited;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import org.apache.tomcat.util.http.fileupload.FileUtils;

import ch.ethz.inf.dbproject.sqlRevisited.SQLType.BaseType;

public class Database {

	private int SERIAL_NUMBER = 1;
		//The serial number of the database. Change this number will result, at next
		//session, of a drop of all tables and total reconstruction of the database.
	
	////
	//Extensions and names
	////
	private String EXT_META_DATA = ".md";
	private String EXT_DATA = ".data";
	private String DB_NAME = "SQLRevisited";
	private String DB_PATH = "Database"+File.separator;
	
	////
	//Constants
	////
	private int MAX_MD_FILE_SIZE = 1024;
	

	public Database() throws Exception {
		
		if (this.newFolder()){
			if(!checkSerialNumber()){
				try {
					dropTables();
					createTables();
				} catch (Exception ex){
					dropTables();
					ex.printStackTrace();
					System.err.println("Failed to create database");
				}
			}
		} else {
			throw new Exception("Cannot create folder of Database");
		}
	}
	
	
	/**
	 * Get the table schema for a particular table
	 * @param tableName the name of the table
	 * @return a TableSchema storing attributes, types and keys of the table
	 * @throws Exception 
	 */
	public TableSchema getTableSchema(String tableName) throws Exception{
		File f = new File(DB_PATH + tableName + EXT_META_DATA);
		if(f.exists() && !f.isDirectory()) {
			//open connection to file
			FileInputStream in = new FileInputStream(DB_PATH + tableName + EXT_META_DATA);
			FileChannel channel = in.getChannel();
			ByteBuffer buf = ByteBuffer.allocateDirect(MAX_MD_FILE_SIZE);
			if (channel.read(buf) == -1) {
				in.close();
				throw new Exception("File too big to be opened.");
			}
				
			//copy bytes into arrays
			buf.flip();
			String[] attributesNames = this.getLine(buf);
			SQLType[] attributesTypes = this.getSQLTypeArrayFromStringArray(this.getLine(buf));
			boolean[] isKey = this.getBooleanArrayFromStringArray(this.getLine(buf));
			buf.clear();
			in.close();
			
			return new TableSchema(tableName, attributesNames, attributesTypes, isKey);
		} else {
			throw new Exception("File "+tableName+" doesn't exist.");
		}
	}
	
	////
	//Database operations
	////
	
	/**
	 * Check if the serial number of the database is the same as in field SERIAL_NUMBER
	 * If not, update automatically this number and return false
	 * @return true if serial number is up to date, false otherwise
	 * @throws IOException
	 */
	private boolean checkSerialNumber() throws IOException{
		File f = new File(DB_PATH + DB_NAME + EXT_META_DATA);
		if(f.exists() && !f.isDirectory()) { 
			if (this.getSerialNumber() == SERIAL_NUMBER)
				return true;
			this.setSerialNumber();
		} else {
			this.setSerialNumber();
		}
		return false;
	}
	
	/**
	 * Get the serial number of the database
	 * @return the serial number of the database
	 * @throws IOException
	 */
	private int getSerialNumber() throws IOException{
		FileInputStream in;
		in = new FileInputStream(DB_PATH + DB_NAME + EXT_META_DATA);
		FileChannel channel = in.getChannel();
        ByteBuffer buf = ByteBuffer.allocateDirect(Integer.SIZE);
        channel.read(buf);
		in.close();
		return buf.getInt(0);
	}
	
	/**
	 * Set the serial number of the database to number stored in field SERIAL_NUMBER
	 * @throws IOException
	 */
	private void setSerialNumber() throws IOException{
		FileOutputStream out;
		out = new FileOutputStream(DB_PATH + DB_NAME + EXT_META_DATA);
		FileChannel channel = out.getChannel();
		ByteBuffer buf = ByteBuffer.allocateDirect(Integer.SIZE);
		buf.putInt(SERIAL_NUMBER);
		buf.rewind();
		channel.write(buf);
		out.close();
	}
	
	/**
	 * Drop (delete files) all tables of the database
	 * @throws IOException 
	 */
	private void dropTables() throws IOException{
		FileUtils.cleanDirectory(new File(DB_PATH));
	}
	
	/**
	 * Create the files which store the tables accordingly to TableSet
	 * @throws IOException
	 */
	private void createTables() throws IOException{
		TableSet ts = new TableSet();
		this.setSerialNumber();
		while (ts.next()){
			//Create two new files regarding entries in TableSet (one for meta-data and one for data)
			
			
			TableSchema tableSchema = ts.getCurrent();
			
			//file for data
			File fileData = new File(DB_PATH + tableSchema.getTableName() + EXT_DATA);
			try {
				boolean createdFileData = fileData.createNewFile();
				if (!createdFileData)
					throw new IOException("File not created");
			} catch (IOException ex){
				ex.printStackTrace();
			}
			
			//file for metadata
			FileOutputStream out;
			out = new FileOutputStream(DB_PATH + tableSchema.getTableName() + EXT_META_DATA);
			FileChannel channel = out.getChannel();
			
			//Get attributes names, types and if they are keys
			String[] attributesNames = tableSchema.getAttributesNames();
			SQLType[] attributesTypes = tableSchema.getAttributesTypes();
			boolean[] isKey = tableSchema.getIfPrimaryKey();
			assert(attributesNames.length == attributesTypes.length && isKey.length == attributesNames.length);
			
			ByteBuffer buf;
			
			//Write attributes names in first line 
			for (int i = 0; i < attributesNames.length; i++){
				byte[] bytes = (attributesNames[i]+" ").getBytes();
				buf = ByteBuffer.allocateDirect(bytes.length).put(bytes);
				buf.rewind();
				channel.write(buf);
			}
			this.newLine(channel);
			
			//Write attributes types in second line
			for (int i = 0; i < attributesTypes.length; i++){
				byte[] bytes = (attributesTypes[i].type.toString()+","+attributesTypes[i].size+" ").getBytes();
				buf = ByteBuffer.allocateDirect(bytes.length).put(bytes);
				buf.rewind();
				channel.write(buf);
			}
			this.newLine(channel);
			
			//Write if attributes are keys in third line
			for (int i = 0; i < isKey.length; i++){
				byte[] bytes = ((isKey[i] ? 1 : 0)+" ").getBytes();
				buf = ByteBuffer.allocateDirect(bytes.length).put(bytes);
				buf.rewind();
				channel.write(buf);
			}
			this.newLine(channel);
			out.close();
		}
	}
	
	/*
	 * Content of the database.
	 * Its constructed as a bag, with a pointer to indicate which
	 * element the bag currently point
	 */
	protected class TableSet{
		
		//Some often used SQLType's :
		private SQLType vc40 = new SQLType(BaseType.Varchar, 40);
		private SQLType vct = new SQLType(BaseType.Varchar, 255);
		private SQLType i = new SQLType(BaseType.Integer);
		private SQLType b = new SQLType(BaseType.Boolean);
		private SQLType d = new SQLType(BaseType.Date);
		private SQLType dt = new SQLType(BaseType.Datetime);
		
		/**
		 * Construct a new TableSet and set position pointer before first element
		 */
		public TableSet(){
			position = -1;
		}
		
		//position of the iterator
		private int position;
		//tables names
		private String[] tablesNames = new String[]{"User", "CaseDetail", "CaseNote", "Person", "PersonNote", "Category", "CategoryForCase", "Suspected", "Conviction"};
		//attributes names
		private String[][] attributesNames = new String[][] {
				{"username", "firstName", "lastName", "password"}, //User
				{"caseId", "title", "street", "city", "zipCode", "isOpen", "date", "description", "authorName"}, //CaseDetail
				{"caseId", "caseNoteId", "text", "date", "authorUsername"}, //CaseNote
				{"personId", "firstName", "lastName", "birthdate"}, //Person
				{"personId", "personNoteId", "text", "date", "authorUsername"}, //PersonNote
				{"name"}, //Category
				{"caseId", "categoryName"}, //CategoryForCase
				{"personId", "caseId"}, //Suspected
				{"convictionId", "personId", "caseId", "startDate", "endDate"} //Conviction
		};
		//attributes types
		private SQLType[][] attributesTypes = new SQLType[][] {
				{this.vc40, this.vc40, this.vc40, this.vc40}, //User
				{this.i, this.vc40, this.vc40, this.vc40, this.vc40, this.b, this.dt, this.vct, this.vc40}, //CaseDetail
				{this.i, this.i, this.vct, this.dt, this.vct}, //CaseNote
				{this.i, this.vc40, this.vc40, this.d}, //Person
				{this.i, this.i, this.vct, this.dt, this.vct}, //PersonNote
				{this.vc40}, //Category
				{this.i, this.vc40}, //CategoryForCase
				{this.i, this.i}, //Suspected
				{this.i, this.i, this.i, this.d, this.d}, //Conviction
				
		};
		//attributes is primary key
		private boolean[][] isPrimaryKey = new boolean[][] {
				{true, false, false, false}, //User
				{true, false, false, false, false, false, false, false}, //CaseDetail
				{true, true, false, false, false}, //CaseNote
				{true, false, false, false}, //Person
				{true, true, false, false, false}, //PersonNote
				{true}, //Category
				{true, true}, //CategoryForCase
				{true, true}, //Suspected
				{true, false, false, false, false} //Conviction
		};
		
		/**
		 * Move the pointer of TableSet to the next element and return if there is
		 * one or not
		 * @return true if there is an other element, else false
		 */
		public boolean next(){
			position++;
			if (position >= tablesNames.length)
				return false;
			return true;
		}
		
		/**
		 * Get currently pointed TableSchema
		 * @return the TableSchema pointed by TableSet
		 */
		public TableSchema getCurrent(){
			return new TableSchema(tablesNames[position], attributesNames[position], attributesTypes[position], isPrimaryKey[position]);
		}	
	}
	
		////
		//Utilities
		////
		
		/**
		 * Read an array of String and turns it into a boolean array
		 * @param array of String representing booleans
		 * @return an array of boolean
		 * @throws Exception
		 */
		private boolean[] getBooleanArrayFromStringArray(String[] array) throws Exception{
			boolean[] ret = new boolean[array.length];
			for (int i = 0; i < array.length; i++){
				if (array[i].equals("1")) { ret[i] = true; }
				else if (array[i].equals("0")) { ret[i] = false; }
				else { throw new Exception("The String " + array[i] + " doesn't represent a boolean"); } 
			}
			return ret;
		}
		
		/**
		 * Read an array of String and turns it into a SQLType array
		 * @param array of String representing SQLType
		 * @return an array of SQLTypes
		 * @throws Exception
		 */
		private SQLType[] getSQLTypeArrayFromStringArray(String[] array) throws Exception{
			SQLType[] ret = new SQLType[array.length];
			for (int i = 0; i < array.length; i++){
				ret[i] = this.getSQLTypeFromString(array[i]);
			}
			return ret;
		}
		
		/**
		 * Return a SQLType from a String
		 * @param type represents a SQLType of the form SQLType.BaseType,SIZE
		 * @return a new SQLType
		 * @throws Exception
		 */
		private SQLType getSQLTypeFromString(String type) throws Exception{
			String[] splitted = type.split(",");
			Integer length = 0;
			try {
				Integer.parseInt(splitted[1]);
			} catch (Exception ex){
				length = null;
			}
			
			if (splitted[0].equals("Integer")){
				return new SQLType(SQLType.BaseType.Integer, length);
			} else if (splitted[0].equals("Char")){
				return new SQLType(SQLType.BaseType.Char, length);
			} else if (splitted[0].equals("Varchar")){
				return new SQLType(SQLType.BaseType.Varchar, length);
			} else if (splitted[0].equals("Date")){
				return new SQLType(SQLType.BaseType.Date, length);
			} else if (splitted[0].equals("Datetime")){
				return new SQLType(SQLType.BaseType.Datetime, length);
			} else if (splitted[0].equals("Boolean")){
				return new SQLType(SQLType.BaseType.Boolean, length);
			} else {
				throw new Exception("The String "+type+" doesn't represent a SQLType");
			}
		}
		
		/**
		 * Get a line from a file through a buffer
		 * @param buf which points to a file
		 * @return a string array of all entries of this line
		 */
		private String[] getLine(ByteBuffer buf){
			List<String> args = new ArrayList<String>();
			String anAttribute = "";
			while(buf.hasRemaining()){
				char aChar = (char) buf.get();
				if (aChar == ' '){
					args.add(anAttribute);
					anAttribute = "";
				} else if (aChar == '\n'){
					break;
				} else {
					anAttribute = anAttribute+aChar;
				}
			}
			String[] ret = (String[]) args.toArray(new String[args.size()]);
			return ret;
		}
		
		/**
		 * Write a new line into the file pointed by a given channel
		 * @param channel which points to a file
		 * @throws IOException
		 */
		private void newLine(FileChannel channel) throws IOException{
			byte[] bytes = "\n".getBytes();
			ByteBuffer buf;
			buf = ByteBuffer.allocateDirect(bytes.length).put(bytes);
			buf.rewind();
			channel.write(buf);
		}
		
		/**
		 * Create a new directory where to put the database
		 * @return true if success, false otherwise
		 */
		private boolean newFolder(){
			return (new File(DB_PATH)).mkdirs();
		}
}