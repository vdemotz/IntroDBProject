package ch.ethz.inf.dbproject.sqlRevisited;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class TableConnection extends DataConnection implements PhysicalTableInterface{

	////
	//Fields
	////
	private StructureConnection structureConnection;
	
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
		this.structureConnection = new StructureConnection(this.tableSchema, dbPath, extMetaData, extData);
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

	public boolean get(ByteBuffer keys, ByteBuffer destination) throws Exception{
		int position = this.structureConnection.getPositionsForKeys(keys);
		if (position == -1)
			return false;
		return this.readFromData(position, tableSchema.getSizeOfEntry(), destination.array());
	}
	
	public boolean succ(ByteBuffer keys, ByteBuffer destination) throws Exception{
		int position = this.structureConnection.getPositionsNextForKeys(keys);
		if (position == -1){
			return false;
		}
		return this.readFromData(position, tableSchema.getSizeOfEntry(), destination.array());
	}
	
	public boolean min(ByteBuffer location) throws Exception{
		int position = this.structureConnection.getFirstPosition();
		if (position == -1)
			return false;
		return this.readFromData(position, this.getTableSchema().getSizeOfEntry(), location.array());
	}
	
	public boolean delete(ByteBuffer keys) throws SQLPhysicalException{
		try{
			return this.structureConnection.deleteElement(keys);
		} catch (Exception ex){
			throw new SQLPhysicalException();
		}
	}
	
	public boolean insert(ByteBuffer object) {
		try{
			int position = structureConnection.insertElement(object);
			byte[] data = new byte[this.tableSchema.getSizeOfEntry()];
			object.get(data);
			this.writeToData(data, position);
			return true;
		} catch (Exception ex){
			ex.printStackTrace();
			return false;
		}
	}
	
	public boolean update(ByteBuffer object) throws SQLPhysicalException{
		if (!(this.delete(object)))
				return false;
		object.rewind();
		int a = object.getInt();
		object.rewind();
		if (!(this.insert(object)))
			return false;
		return true;
	}
	
	/**
	 * Get the connection to the particular structure of this table
	 */
	public StructureConnection getStructureConnection(){
		return this.structureConnection;
	}

	@Override
	public TableIterator getIterator(ByteBuffer key) throws SQLPhysicalException {
		return new PhysicalTableIterator(key, this);
	}

	@Override
	public TableIterator getIteratorFirst() throws SQLPhysicalException {
		return new PhysicalTableIterator(null, this);
	}
}
