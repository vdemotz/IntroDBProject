package ch.ethz.inf.dbproject.sqlRevisited;

import java.io.File;
import java.io.RandomAccessFile;
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
	
	////
	//Read / write related methods
	////
	
	/**
	 * Read length bytes from table at given position into the destination ByteBuffer
	 */
	protected boolean readFromData(int position, int length, byte[] destination) throws SQLPhysicalException{
		try{
			MappedByteBuffer buf = this.channel.map(FileChannel.MapMode.READ_WRITE, position, length);
			buf.get(destination);
			return true;
		} catch (Exception ex){
			System.err.println("Unable to read data from "+this.tableSchema.getTableName());
			ex.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Write a given piece of data into the database.
	 * @param data to be written
	 * @param position the offset (take care of alignment!)
	 * @return true if succeed, false otherwise
	 */
	protected boolean writeToData(byte[] data, int position) throws SQLPhysicalException{
		try{
			MappedByteBuffer buf = this.channel.map(FileChannel.MapMode.READ_WRITE, position, data.length);
			buf.put(data);
			return true;
		} catch (Exception ex){
			System.err.println("Unable to write data to "+this.tableSchema.getTableName());
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
