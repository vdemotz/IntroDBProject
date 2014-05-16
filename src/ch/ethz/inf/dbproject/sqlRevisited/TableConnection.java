package ch.ethz.inf.dbproject.sqlRevisited;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class TableConnection {

	////
	//Fields
	////
	private TableSchema tableSchema;
	private RandomAccessFile raf;
	private FileChannel channel;
	private String DB_PATH;
	private String EXT_META_DATA;
	private String EXT_DATA;
	private Serializer serializer;
	
	////
	//Constructor, finalize
	////
	/**
	 * Create a new direct connection to a table
	 * @param ts The table schema of the table which represents every meta data
	 * @param db_path DB specific : path to folder of database
	 * @param ext_meta_data DB specific : extension of meta data files
	 * @param ext_data DB specific : extension of data files
	 */
	public TableConnection(TableSchema ts, String db_path, String ext_meta_data, String ext_data) throws Exception{
		this.tableSchema = ts;
		this.DB_PATH = db_path;
		this.EXT_META_DATA = ext_meta_data;
		this.EXT_DATA = ext_data;
		this.raf = this.getRandomAccesFile(this.tableSchema.getTableName(), "rw", false);
		this.channel = raf.getChannel();
	}
	
	@Override
	protected void finalize() throws Throwable{
		channel.close();
		raf.close();
		super.finalize();
	}
	
	////
	//Public methods
	////
	
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
	//Read / write related methods
	////
	
	/**
	 * Read length bytes from table at given position into the destination ByteBuffer
	 */
	private boolean readFromData(int position, int length, ByteBuffer destination) throws Exception{
		return false;
	}
	
	/**
	 * Write a given piece of data into the database. It doesn't check size!
	 * @param data to be written
	 * @param position the offset (take care of alignment!)
	 * @return true if succeed, false otherwise
	 */
	private boolean writeToData(byte[] data, int position, String tableName) throws Exception{
		try{
			MappedByteBuffer buf = this.channel.map(FileChannel.MapMode.READ_WRITE, position, data.length);
			buf.put(data);
			return true;
		} catch (Exception ex){
			System.err.println("Unable to write data to "+tableName);
			ex.printStackTrace();
			return false;
		}
	}
	
	////
	//Files related methods
	////
	/**
	 * Get a new file for random access to read from and write to the database
	 * @param mode the mode to write (see RandomAccessFile constructor)
	 */
	private RandomAccessFile getRandomAccesFile(String tableName, String mode, boolean metaData) throws Exception{
		String filename = metaData ? DB_PATH + tableName + EXT_META_DATA : DB_PATH + tableName + EXT_DATA;
		
		//Check if table exists
		File f = new File(filename);
		if(!f.exists()) 
			throw new Exception("Table doesn't exist.");
		return new RandomAccessFile(filename, mode);
	}
}
