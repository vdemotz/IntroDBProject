package ch.ethz.inf.dbproject.sqlRevisited;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

public class TableConnection extends DataConnection {

	////
	//Fields
	////
	private Serializer serializer;
	private StructureConnection dataStructure;
	
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
	public TableConnection(TableSchema ts, String dbPath, String extMetaData, String extData) throws Exception{
		this.tableSchema = ts;
		this.DB_PATH = dbPath;
		this.EXT_META_DATA = extMetaData;
		this.EXT_DATA = extData;
		this.raf = this.getRandomAccesFile(this.tableSchema.getTableName(), "rw", false);
		this.channel = raf.getChannel();
		this.dataStructure = new StructureConnection(this.tableSchema, dbPath, extMetaData, extData);
		this.serializer = new Serializer();
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
		List<Integer> position = this.dataStructure.getPositionsForKeys(keys, numberKeys);
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
}
