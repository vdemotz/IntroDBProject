package ch.ethz.inf.dbproject.sqlRevisited;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public abstract class DataConnection {
	
	protected FileChannel channel;
	protected String DB_PATH;
	protected String EXT_META_DATA;
	protected String EXT_DATA;
	protected RandomAccessFile raf;
	protected TableSchema tableSchema;
	protected Serializer serializer;
	
	/**
	 * Compare two keys. Be aware of positions of pointer of key1 and key2
	 * @param numberKeys number of keys compared
	 * @return true if key1 < key2, false otherwise
	 */
	protected boolean compareKeys(ByteBuffer key1, ByteBuffer key2, SQLType type) throws Exception{
		boolean ret;
		if (type.type == SQLType.BaseType.Integer){
			ret = (key1.getInt() < key2.getInt()) ? true : false;
		} else if (type.type == SQLType.BaseType.Varchar){
			//O.o Long line
			ret = (this.serializer.getStringFromByteBuffer(key1, type.byteSizeOfType()).compareTo(this.serializer.getStringFromByteBuffer(key2, type.byteSizeOfType())) < 0) ? true : false;
		} else {
			throw new Exception("Key type not supported " + type.toString());
		}
		return ret;
	}
	
	
	////
	//Read / write related methods
	////
	
	/**
	 * Read length bytes from table at given position into the destination ByteBuffer
	 */
	protected boolean readFromData(int position, int length, ByteBuffer destination) throws Exception{
		return false;
	}
	
	/**
	 * Write a given piece of data into the database. It doesn't check size!
	 * @param data to be written
	 * @param position the offset (take care of alignment!)
	 * @return true if succeed, false otherwise
	 */
	protected boolean writeToData(byte[] data, int position, String tableName) throws Exception{
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
	protected RandomAccessFile getRandomAccesFile(String tableName, String mode, boolean metaData) throws Exception{
		String filename = metaData ? DB_PATH + tableName + EXT_META_DATA : DB_PATH + tableName + EXT_DATA;
		
		//Check if table exists
		File f = new File(filename);
		if(!f.exists()) 
			throw new Exception("Table doesn't exist.");
		return new RandomAccessFile(filename, mode);
	}
}
