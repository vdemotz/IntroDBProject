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

public class Database {

	private int SERIAL_NUMBER = 1;
		//The serial number of the database. Change this number will result, at next
		//session, of a drop of all tables and total reconstruction of the database.
	
	////
	//Extensions and names
	////
	private static final String EXT_META_DATA = ".md";
	private static final String EXT_DATA = ".data";
	private final String DB_NAME = "SQLRevisited";
	private static final String DB_PATH = "Database"+File.separator;
	private final String directoryPath;
	
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
	/**
	 * Create a new Database object. This object represents a whole database, with every table
	 * declared in table set. It has a serial number to be changed if TableSet change. The change
	 * will destroy every table and rebuild the whole database from scratch.
	 * 
	 * To access a particular table, try "getTableConnection". Please refer to TableConnection
	 * documentation for more information on how to read/write to this particular table.
	 */
	public Database() throws Exception {
		directoryPath = DB_PATH;
		init();
	}
	
	public Database(String baseDirectory) throws Exception {
		if (baseDirectory == null || baseDirectory.length() == 0) {
			directoryPath = DB_PATH;
		} else {
			directoryPath = baseDirectory + File.separator + DB_PATH;
		}
		init();
		
	}
	
	private void init() throws Exception {
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
			throw new Exception("Cannot create folder of Database at" + directoryPath );
		}
	}
	
	////
	//Public methods, meant accessed through a (singleton) connection
	////
	/**
	 * Return a table schema providing the name of this table (without extension)
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	public TableSchema getTableSchema(String tableName) throws Exception{
		tableName = tableName.toLowerCase();
		for (int i = 0; i < tablesSchema.length; i++){
			if (tablesSchema[i].getTableName().equals(tableName))
				return tablesSchema[i];
		}
		return null;
	}
	
	/**
	 * Open new connections to all tables in database
	 * @return a list of all table connections
	 * @throws Exception
	 */
	public List<PhysicalTableInterface> getAllTablesConnections() throws Exception {
		List<PhysicalTableInterface> ret = new ArrayList<PhysicalTableInterface>();
		for (int i = 0; i < tablesSchema.length; i++){
			ret.add(new TableConnection(tablesSchema[i], this.directoryPath, EXT_META_DATA, EXT_DATA));
		}
		return ret;
	}
	
	/**
	 * Open one TableConnection, given the name of this table (without extension)
	 * @param tableName
	 * @return a new direct connection to the table
	 * @throws Exception
	 */
	public TableConnection getTableConnection(String tableName) throws SQLException{
		try {
			return new TableConnection(this.getTableSchema(tableName.toLowerCase()), this.directoryPath, EXT_META_DATA, EXT_DATA);
		} catch (Exception e) {
			throw new SQLPhysicalException();
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
		File f = new File(directoryPath + DB_NAME + EXT_META_DATA);
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
		in = new FileInputStream(directoryPath + DB_NAME + EXT_META_DATA);
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
		out = new FileOutputStream(directoryPath + DB_NAME + EXT_META_DATA);
		FileChannel channel = out.getChannel();
		ByteBuffer buf = ByteBuffer.allocateDirect(Integer.SIZE);
		buf.putInt(SERIAL_NUMBER);
		buf.rewind();
		channel.write(buf);
		out.close();
	}
	
	/**
	 * Drop (delete files) all tables of the database. Public only for tests purposes.
	 * @throws IOException 
	 */
	public void dropTables() throws IOException{
		FileUtils.cleanDirectory(new File(directoryPath));
	}
	
	/**
	 * Create the files which store the tables accordingly to TableSet
	 */
	private void createTables() throws Exception{
		TableSet ts = new TableSet();
		this.setSerialNumber();
		while (ts.next()){
			//Create two new files regarding entries in TableSet (one for meta-data and one for data)
			
			
			TableSchema tableSchema = ts.getCurrent();
			
			//file for data
			File fileData = new File(directoryPath + tableSchema.getTableName() + EXT_DATA);
			try {
				boolean createdFileData = fileData.createNewFile();
				if (!createdFileData)
					throw new IOException("File not created");
			} catch (IOException ex){
				ex.printStackTrace();
			}
			
			//file for metadata
			FileOutputStream out = new FileOutputStream(directoryPath + tableSchema.getTableName() + EXT_META_DATA, true);
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
	 * Create a new directory where to put the database if it does not exist yet
	 * @precondition directoryPath is set to a valid directory string
	 * @return true if successful, false otherwise
	 */
	private boolean newFolder(){
		if (new File(directoryPath).exists()) {
		    return true;
		} else {
			return (new File(directoryPath)).mkdirs();
		}
	}
	
	/**
	 * Get all tables schemas from database -- Maybe to be removed
	 * @return an array of TableSchema
	 */
	private TableSchema[] getTablesSchema() throws SQLPhysicalException {
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
			throw new SQLPhysicalException();
		}
	}
	
	/**
	 * Read a table schema from database
	 * @param tableName the name of the table
	 * @return a TableSchema representing meta data of the table
	 * @throws Exception
	 */
	private TableSchema getTableSchemaFromDB(String tableName) throws Exception{
		File f = new File(directoryPath + tableName + EXT_META_DATA);
		if(f.exists() && !f.isDirectory()) {
			//open connection to file
			FileInputStream in = new FileInputStream(directoryPath + tableName + EXT_META_DATA);
			FileChannel channel = in.getChannel();
			ByteBuffer buf = ByteBuffer.allocateDirect(MAX_MD_FILE_SIZE);
			if (channel.read(buf) == -1) {
				in.close();
				throw new Exception("File too big to be opened.");
			}
				
			//copy bytes into arrays
			buf.flip();
			String[] attributesNames = this.getLine(buf);
			SQLType[] attributesTypes = Serializer.getSQLTypeArrayFromStringArray(this.getLine(buf));
			boolean[] isKey = Serializer.getBooleanArrayFromStringArray(this.getLine(buf));
			buf.clear();
			in.close();
			
			return new TableSchema(tableName, attributesNames, attributesTypes, isKey);
		} else {
			throw new Exception("File "+tableName+" doesn't exist.");
		}
	}
}