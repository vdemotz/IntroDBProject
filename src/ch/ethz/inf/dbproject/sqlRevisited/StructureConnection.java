package ch.ethz.inf.dbproject.sqlRevisited;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import ch.ethz.inf.dbproject.Pair;

public class StructureConnection extends DataConnection{
	
	/*
	 * A cached array-structure for lookup in a table
	 */
	private final int ELEMENT_SIZE;	
	private final int KEYS_SIZE;
	private final int OFFSET_META_DATA = 1024;
	private final int MAXIMAL_META_DATA_SIZE = 8192;
	private final int HEADER_KEY_SIZE = 8;
	private final int INT_BYTES_SIZE = 4;
	private List<Pair<byte[], Integer>> elementsPositions;
	
	/**
	 * Open a new connection to a structure of a table. This structure can be changed independently of the table
	 * and you can have more than one structure per table. In that particular case, be aware of synchronization issues.
	 * 
	 * Inherits from DataConnection for particular ways of write/read to files.
	 * 
	 * @param tableSchema a tableSchema of the table
	 * @param dbPath path to the repository of the database
	 * @param extMetaData particular extension of meta data files for this database
	 * @param extData particular extension of data files for this database
	 */
	public StructureConnection(TableSchema tableSchema, String dbPath, String extMetaData, String extData) throws Exception{
		this.EXT_DATA = extData;
		this.EXT_META_DATA = extMetaData;
		this.DB_PATH = dbPath;
		this.serializer = new Serializer();
		this.ELEMENT_SIZE = tableSchema.getSizeOfEntry()+INT_BYTES_SIZE;
		this.KEYS_SIZE = tableSchema.getSizeOfKeys();
		this.tableSchema = tableSchema;
		this.raf = this.getRandomAccesFile(this.tableSchema.getTableName(), "rw", true);
		this.channel = raf.getChannel();
		this.buf = this.channel.map(FileChannel.MapMode.READ_WRITE, OFFSET_META_DATA, MAXIMAL_META_DATA_SIZE);
		this.elementsPositions = this.instantiateElementsPositions();
	}
	
	
	
	@Override
	protected void finalize() throws Throwable{
		channel.close();
		raf.close();
		super.finalize();
	}
	
	/**
	 * Get the position of the first tuple of the table
	 */
	public int getFirstPosition() throws Exception{
		if (elementsPositions.size() > 0){
			return elementsPositions.get(0).second;
		}
		return -1;
	}
	
	/**
	 * Get the position in the table of the tuple matched by keys
	 */
	public int getPositionsForKeys(ByteBuffer keys) throws Exception{
		for (int i = 0; i < elementsPositions.size(); i++){
			if (serializer.compareKeys(keys, ByteBuffer.wrap(elementsPositions.get(i).first), this.tableSchema) == 0)
				return elementsPositions.get(i).second;
		}
		return -1;
	}
	
	/**
	 * Get the position in the table of the next tuple matched by keys
	 */
	public int getPositionsNextForKeys(ByteBuffer keys) throws Exception{
		for (int i = 0; i < elementsPositions.size(); i++){
			if (serializer.compareKeys(keys, ByteBuffer.wrap(elementsPositions.get(i).first), this.tableSchema) == 0)
				if (i+1 < elementsPositions.size()){
					int returnPosition = elementsPositions.get(i+1).second;
					return returnPosition;
				}
				else
					break;
		}
		return -1;
	}
	
	/**
	 * Delete one element. Note that it's not really deleted, just freed place in structure and no more accessible
	 */
	public boolean deleteElement(ByteBuffer keys) throws Exception{
		int position = -1;
		for (int i = 0; i < elementsPositions.size(); i++){
			if (serializer.compareKeys(keys, ByteBuffer.wrap(elementsPositions.get(i).first), this.tableSchema) == 0) {
				position = i;
				break;
			}
		}
		if (position == -1){
			System.err.println("Key not found");
			return false;
		}
		this.shiftData((this.KEYS_SIZE+8)*(position+1)+this.OFFSET_META_DATA, -(this.HEADER_KEY_SIZE+this.KEYS_SIZE));
		elementsPositions.remove(position);
		return true;
	}
	
	/**
	 * Get the position in the table where to write this object and update the index accordingly
	 */
	public int insertElement(ByteBuffer object) throws Exception{
		int whereToWrite = 0;
		for (int i = 0; i < elementsPositions.size(); i++){
			if ((int)elementsPositions.get(i).second >= whereToWrite){
				whereToWrite = elementsPositions.get(i).second+this.ELEMENT_SIZE;
			}
		}
		int position = this.getPositionAndInsert(object, whereToWrite);
		this.shiftData(position, this.KEYS_SIZE+8);
		this.writeToData(this.createKeysFromByteBufferAndPosition(object, whereToWrite).array(), position);
		object.rewind();
		return whereToWrite;
	}
	
	/**
	 * Get an in-order array of all positions of elements in the table
	 * @return in-order array of positions
	 */
	public int[] getPositionsInOrder(){
		int size = this.elementsPositions.size();
		if (size <= 0)
			return null;
		int[] ret = new int[size];
		for (int i = 0; i < size; i++)
			ret[i] = elementsPositions.get(i).second;
		return ret;
	}
	
	////
	//Private methods
	////
	
	/**
	 * Create a writable key from an object
	 */
	private ByteBuffer createKeysFromByteBufferAndPosition(ByteBuffer object, int positionToWrite){
		ByteBuffer ret = ByteBuffer.allocate(this.KEYS_SIZE+this.HEADER_KEY_SIZE);
		ret.putInt(1);
		for (int i = 0; i < this.KEYS_SIZE; i++){
			ret.put(object.get());
		}
		ret.putInt(positionToWrite);
		ret.rewind();
		object.rewind();
		return ret;
	}
	
	/**
	 * Shift Data of the structure from position to position + numberBytes
	 * @param position a position in bytes where to get the data
	 * @param numberBytes an offset (positive or negative) to add to position
	 */
	private void shiftData(int position, int numberBytes) throws SQLPhysicalException{
		byte[] tmp = new byte[MAXIMAL_META_DATA_SIZE-(KEYS_SIZE+8+position)];
		try{
			buf.position(position);
			buf.get(tmp, 0, tmp.length);
			buf.position(position+numberBytes);
			buf.put(tmp);
		} catch (Exception ex){
			throw new SQLPhysicalException();
		}
		return;
	}
	
	/**
	 * Truncate an object to get only keys, according to the tableSchema entries
	 * @param object ByteBuffer
	 * @return a ByteBuffer which contains key of object
	 */
	private ByteBuffer getKeysFromByteBuffer(ByteBuffer object){
		int position = object.position();
		ByteBuffer ret = ByteBuffer.allocate(this.KEYS_SIZE);
		for (int i = 0; i < this.KEYS_SIZE;i++)
			ret.put(object.get());
		ret.rewind();
		object.position(position);
		return ret;
	}
	
	/**
	 * Get the physical position in the structure of an entry and insert it into the structure
	 * @param object an object to write into the database
	 * @param whereToWrite the position in the table
	 * @return a position where to write this entry in the structure
	 * @throws Exception
	 */
	private int getPositionAndInsert(ByteBuffer object, int whereToWrite) throws Exception{
		int i = 0;
		ByteBuffer keyToInsert = this.getKeysFromByteBuffer(object);
		while(i < elementsPositions.size() && serializer.compareKeys(keyToInsert, ByteBuffer.wrap(elementsPositions.get(i).first), tableSchema) > 0)
			i++;
		elementsPositions.add(i, new Pair<byte[], Integer>(keyToInsert.array(), whereToWrite));
		return i*(this.KEYS_SIZE+this.HEADER_KEY_SIZE);
	}
	
	/**
	 * Cache elements positions
	 */
	private List<Pair<byte[], Integer>> instantiateElementsPositions() throws IOException{
		List<Pair<byte[], Integer>> elementsPositions = new ArrayList<Pair<byte[], Integer>>();
		buf.rewind();
		byte[] bytes = new byte[buf.capacity()];
		buf.get(bytes);
		int i = 0;
		while ((int)bytes[i+3] == 1){
			elementsPositions.add(new Pair<byte[], Integer>(Arrays.copyOfRange(bytes, i+INT_BYTES_SIZE, i+INT_BYTES_SIZE+KEYS_SIZE), Serializer.getIntegerFromByteArray(Arrays.copyOfRange(bytes, i+INT_BYTES_SIZE+KEYS_SIZE, i+HEADER_KEY_SIZE+KEYS_SIZE))));
			i += KEYS_SIZE+HEADER_KEY_SIZE;
		}
		buf.rewind();
		return elementsPositions;
	}
}
