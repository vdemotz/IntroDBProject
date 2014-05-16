package ch.ethz.inf.dbproject.sqlRevisited;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class TableConnection {

	private TableSchema tableSchema;
	private RandomAccessFile raf;
	private FileChannel channel;
	private String DB_PATH;
	private String EXT_META_DATA;
	private String EXT_DATA;
	private Serializer serializer;
	
	public TableConnection(){
		
	}
	
	/**
	 * Get the TableSchema for this table
	 */
	public TableSchema getTableSchema(){
		return this.tableSchema;
	}
	
	/**
	 * Get the channel to read/write to this table
	 */
	public FileChannel getChannel(){
		return this.channel;
	}

	/**
	 * Write in location tuples determined by numberKeys and keys
	 * @param keys a ByteBuffer where the keys are written. Take care of position of pointer
	 * @param numberKeys the number of keys to read
	 * @param location where to write tuples
	 * @return true if operation succeed (write at least one tuple), false otherwise
	 */
	public boolean get(ByteBuffer keys, int numberKeys, ByteBuffer location){
		return false;
	}
	
	/**
	 * Write in location successor of tuple determined by numberKeys and keys (keys should determine one or zero tuple)
	 * @param keys a ByteBuffer where the keys are written. Take care of position of pointer
	 * @param location where to write tuples
	 * @return true if operation succeed (write a tuple), false otherwise
	 */
	public boolean succ(ByteBuffer keys, ByteBuffer location){
		return false;
	}
	
	/**
	 * Write the first tuple of the table into location
	 * @param location where to write tuples
	 * @return true if operation succeed (write a tuple), false otherwise
	 */
	public boolean min(ByteBuffer location){
		return false;
	}
	
	/**
	 * Delete tuples determined by keys from the table
	 * @param keys a ByteBuffer where the keys are written. Take care of position of pointer
	 * @param numberKeys the number of keys to read
	 * @return true if operation succeed (delete at least one tuple), false otherwise
	 */
	public boolean delete(ByteBuffer keys, int numberKeys){
		return false;
	}
	
	/**
	 * Insert a new tuple into the table. 
	 * @param object the object to write. It should be exactly of the form describe in TableSchema
	 * @return true if operation succeed (entry written in database), false otherwise
	 */
	public boolean insert(ByteBuffer object){
		return false;
	}
	
	/**
	 * Update a tuple of the table
	 * @param object The object that will replace the old one. The database will update the objects that has the same keys of this object
	 * @return true if operation succeed, false otherwise
	 */
	public boolean update(ByteBuffer object){
		return false;
	}
	
	////
	//
	////
	
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
		return serializer.createObjectFromBytesArrayAndTableName(ret, tableSchema.getAttributesTypes());
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
