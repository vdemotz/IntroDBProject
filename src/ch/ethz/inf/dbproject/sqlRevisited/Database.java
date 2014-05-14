package ch.ethz.inf.dbproject.sqlRevisited;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import ch.ethz.inf.dbproject.sqlRevisited.SQLType.BaseType;

public class Database {

	private int SERIAL_NUMBER = 3;
		//The serial number of the database. Change this number will result, at next
		//session, of a drop of all tables and total reconstruction of the database.
	
	////
	//Extensions and names
	////
	private String EXT_META_DATA = ".md";
	private String EXT_DATA = ".data";
	private String DB_NAME = "SQLRevisited";
	private String DB_PATH = "Database/";
	
	////
	//Constants
	////
	private int MAX_MD_FILE_SIZE = 1024;
	
	
	public Database() throws IOException {
		
		if(!checkSerialNumber()){
			dropTables();
			createTables();
		}
		getTableSchema("User");
	}
	
	/**
	 * Get the table schema for a particular table
	 * @param tableName the name of the table
	 * @return a TableSchema storing attributes, types and keys of the table
	 * @throws IOException
	 */
	public TableSchema getTableSchema(String tableName) throws IOException{
		File f = new File(DB_PATH + tableName + EXT_META_DATA);
		if(f.exists() && !f.isDirectory()) {
			FileInputStream in = new FileInputStream(DB_PATH + tableName + EXT_META_DATA);
			FileChannel channel = in.getChannel();
			ByteBuffer buf = ByteBuffer.allocateDirect(MAX_MD_FILE_SIZE);
			int bytesRead = channel.read(buf);
			System.out.println("Stored in file : ");
			buf.flip();
			while(buf.hasRemaining()){
				System.out.print((char) buf.get());
			}
			buf.clear();
			System.out.println("\r\nNumber of bytes read : "+bytesRead);
			in.close();
			return null;
		} else {
			return null;
		}
	}
	
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
	 */
	private void dropTables(){
		
	}
	
	/**
	 * Create the files which store the tables accordingly to TableSet
	 * @throws IOException
	 */
	private void createTables() throws IOException{
		TableSet ts = new TableSet();
		while (ts.next()){
			System.out.println("Try to write a tableSchema");
			TableSchema tableSchema = ts.getCurrent();
			
			FileOutputStream out;
			out = new FileOutputStream(DB_PATH + tableSchema.getTableName() + EXT_META_DATA);
			FileChannel channel = out.getChannel();
			
			System.out.println("File created, get attributes");
			
			String[] attributesNames = tableSchema.getAttributesNames();
			SQLType[] attributesTypes = tableSchema.getAttributesTypes();
			boolean[] isKey = tableSchema.getIfPrimaryKey();
			assert(attributesNames.length == attributesTypes.length && isKey.length == attributesNames.length);
			
			System.out.println("Try to write attributes names");
			
			for (int i = 0; i < attributesNames.length; i++){
				byte[] bytes = (attributesNames[i]+" ").getBytes();
				ByteBuffer buf = ByteBuffer.allocateDirect(bytes.length);
				buf.put(bytes);
				buf.rewind();
				channel.write(buf);
			}
			
			System.out.println("Attributes names created, write attributes types");
			
			for (int i = 0; i < attributesTypes.length; i++){
				byte[] bytes = (attributesTypes[i]+" ").getBytes();
				ByteBuffer buf = ByteBuffer.allocateDirect(bytes.length);
				buf.put(bytes);
				buf.rewind();
				channel.write(buf);
			}
			
			System.out.println("Attributes types created, write attributes booleans");

			for (int i = 0; i < isKey.length; i++){
				byte[] bytes = (isKey[i]+" ").getBytes();
				ByteBuffer buf = ByteBuffer.allocateDirect(bytes.length);
				buf.put(bytes);
				buf.rewind();
				channel.write(buf);
			}
			
			System.out.println("Done");
			
			out.close();
		}
	}
	
	/*
	 * Content of the database.
	 */
	protected class TableSet{
		public TableSet(){
			position = -1;
		}
		
		private int position;
		private String[] tablesNames = new String[]{"User"};
		private String[][] attributesNames = new String[][] {
				{"username", "firstName", "lastName", "password"},
		};
		private SQLType[][] attributesTypes = new SQLType[][] {
				{new SQLType(BaseType.Varchar, 40), new SQLType(BaseType.Varchar, 40), new SQLType(BaseType.Varchar, 40), new SQLType(BaseType.Varchar, 40)},
		};
		private boolean[][] isPrimaryKey = new boolean[][] {
				{true, false, false, false}
		};
		
		public boolean next(){
			position++;
			if (position >= tablesNames.length)
				return false;
			return true;
		}
		
		public TableSchema getCurrent(){
			return new TableSchema(tablesNames[position], attributesNames[position], attributesTypes[position], isPrimaryKey[position]);
		}	
	}
}