package ch.ethz.inf.dbproject.sqlRevisited;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
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

	////
	//Very used values
	////
	private TableSchema[] tablesSchema;
	

	////
	//Constructor -- may take some time (Principle based on SQLite)
	////
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
			tablesSchema = this.getTablesSchema();
		} else {
			throw new Exception("Cannot create folder of Database");
		}
	}
	
	////
	//Public methods, meant accessed through a (singleton) connection
	////
	
	public TableSchema getTableSchema(String tableName) throws Exception{
		for (int i = 0; i < tablesSchema.length; i++){
			if (tablesSchema[i].getTableName().equals(tableName))
				return tablesSchema[i];
		}
		return null;
	}
	
	public Object min(String tableName) throws Exception{
		
		//Get the number of bytes per entry 
		int sizeEntry = this.getTableSchema(tableName).getSizeOfEntry();
		
		//Create connection to file and allocate buffer
		FileInputStream in = this.getFileInputStream(tableName, true);
		FileChannel channel = in.getChannel();
		ByteBuffer buf = ByteBuffer.allocateDirect(sizeEntry);
		if (channel.read(buf) == -1) {
			in.close();
			throw new Exception("File too big to be opened.");
		}
			
		//Create object from bytes array
		buf.flip();
		Object ret = this.createObjectFromByteBufferAndTableName(buf, tableName);
		buf.clear();
		in.close();
		return ret;
	}
	
	public Object get(Object[] primaryKeys, String tableName){
		return null;
	}
	
	public Object succ(Object[] primaryKeys, String tableName){
		return null;
	}
	
	public boolean delete(Object[] primaryKeys, String tableName){
		return false;
	}

	public boolean insert(Object[] valuesToInsert, String[] attributesNames, String tableName) throws Exception{
		
		//actual implementation only for tests purposes
		int offset = 0;
		for (int i = 0; i < valuesToInsert.length; i++){
			byte[] data = this.getByteArrayFromObject(valuesToInsert[i], new SQLType(BaseType.Varchar, 40));
			this.writeToData(data, offset, tableName);
			offset = offset + data.length;
		}
		
		
		return false;
	}
	
	public boolean update(Object[] primaryKeys, Object toUpdate, String tableName){
		return false;
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
			length = Integer.parseInt(splitted[1]);
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
	 * Create a new directory where to put the database if not exist
	 * @return true if success, false otherwise
	 */
	private boolean newFolder(){
		if (new File(DB_PATH).exists()) {
		    return true;
		} else {
			return (new File(DB_PATH)).mkdirs();
		}
	}
	
	/**
	 * Get all tables schemas from database
	 * @return an array of TableSchema
	 */
	private TableSchema[] getTablesSchema(){
		try{
			TableSet ts = new TableSet();
			String[] tablesNames = ts.getTablesNames();
			TableSchema[] ret = new TableSchema[ts.getNumberTables()];
			int i = 0;
			while(ts.next()){
				ret[i] = this.getTableSchemaFromDB(tablesNames[i]);
				i++;
			}
			return ret;
		} catch (Exception ex){
			System.err.println("Failed to get all tables schemas.");
			ex.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Read a table schema from database
	 * @param tableName the name of the table
	 * @return a TableSchema representing meta data of the table
	 * @throws Exception
	 */
	private TableSchema getTableSchemaFromDB(String tableName) throws Exception{
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
	
	/**
	 * Create a new object of the class given by table name and written in ByteBuffer
	 * @param data represents an object
	 * @param tableName the type of the object
	 * @return an object of type tableName.class
	 */
	private Object createObjectFromByteBufferAndTableName(ByteBuffer data, String tableName){
		return null;
	}
	
	/**
	 * Create a new object of the class given by table name and written in byte array
	 * @param data represents an object
	 * @param tableName the type of the object
	 * @return an object of type tableName.class
	 */
	private Object createObjectFromBytesArrayAndTableName(byte[] data, String tableName){
		return null;
	}
	
	/**
	 * Transform an object into a byte array to write into the database
	 * @param data the object to transform
	 * @return a byte array to write to database
	 */
	private byte[] getByteArrayFromObject(Object data, SQLType type) throws Exception{
		byte[] ret = null;
		if(type.type == BaseType.Integer){
			ret = ByteBuffer.allocate(type.byteSizeOfType()).putInt((int) data).array();
		} else if (type.type == BaseType.Boolean){
			boolean dataB = (boolean) data;
			ret = dataB ? "1".getBytes() : "0".getBytes();
		} else if (type.type == BaseType.Char){
			ret = ByteBuffer.allocate(type.byteSizeOfType()).putChar((char) data).array();
		} else if (type.type == BaseType.Varchar || type.type == BaseType.Date || type.type == BaseType.Datetime){
			ret = ((String)data).getBytes();
		} else {
			throw new Exception("Not accepted SQLType");
		}
		return ret;
	}
	
	/**
	 * Read from memory
	 */
	private Object readFromData(int position, int size, String tableName) throws Exception{
		FileInputStream in = this.getFileInputStream(tableName, false);
		byte[] ret = new byte[size];
		try{
			in.read(ret, position, size);
		} catch (Exception ex){
			System.err.println("Unable to read from data "+tableName);
			throw ex;
		}
		return this.createObjectFromBytesArrayAndTableName(ret, tableName);
	}
	
	/**
	 * Write a given piece of data into the database. It doesn't check size!
	 * @param data to be written
	 * @param position the offset (take care of alignment!)
	 * @return true if succeed, false otherwise
	 */
	private boolean writeToData(byte[] data, int position, String tableName) throws Exception{
	    RandomAccessFile raf = this.getRandomAccesFile(tableName, "rw", false);
		try{
			FileChannel channel = raf.getChannel();
			MappedByteBuffer buf = channel.map(FileChannel.MapMode.READ_WRITE, position, data.length);
			buf.put(data);
			raf.close();
			return true;
		} catch (Exception ex){
			System.err.println("Unable to write data to "+tableName);
			ex.printStackTrace();
			raf.close();
			return false;
		}
	}
	
	/**
	 * Get a new file for random access to read from and write to the database
	 * @param mode the mode to write (see RandomAccessFile constructor)
	 */
	private RandomAccessFile getRandomAccesFile(String tableName, String mode, boolean metaData) throws Exception{
		String filename = metaData ? DB_PATH + tableName + EXT_META_DATA : DB_PATH + tableName + EXT_DATA;
		
		//Check if table exist
		File f = new File(filename);
		if(!f.exists()) 
			throw new Exception("Table doesn't exist.");
		return new RandomAccessFile(filename, mode);
	}
	
	/**
	 * Get a new file to read from the database
	 */
	private FileInputStream getFileInputStream(String tableName, boolean metaData) throws Exception{
		
		String filename = metaData ? DB_PATH + tableName + EXT_META_DATA : DB_PATH + tableName + EXT_DATA;
		
		//Check if table exist
		File f = new File(filename);
		if(!f.exists()) 
			throw new Exception("Table doesn't exist.");
		return new FileInputStream(filename);
	}
	
	/**
	 * Get a new file to write from the database
	 */
	private FileOutputStream getFileOutputStream(String tableName, boolean metaData) throws Exception{
		
		String filename = metaData ? DB_PATH + tableName + EXT_META_DATA : DB_PATH + tableName + EXT_DATA;
		
		
		//Check if table exist
		File f = new File(filename);
		if(!f.exists()) 
			throw new Exception("Table doesn't exist.");
		return new FileOutputStream(filename, true);
	}
}