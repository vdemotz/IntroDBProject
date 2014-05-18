package ch.ethz.inf.dbproject.sqlRevisited;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
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
	private int OFFSET_META_DATA = 1024;
	private int MAXIMAL_META_DATA_SIZE = 4096;
	private List<Pair<byte[], Integer>> elementsPositions;
	
	public StructureConnection(TableSchema tableSchema, String dbPath, String extMetaData, String extData) throws Exception{
		this.EXT_DATA = extData;
		this.EXT_META_DATA = extMetaData;
		this.DB_PATH = dbPath;
		this.serializer = new Serializer();
		this.ELEMENT_SIZE = tableSchema.getSizeOfEntry()+4;
		this.KEYS_SIZE = tableSchema.getSizeOfKeys();
		this.tableSchema = tableSchema;
		this.raf = this.getRandomAccesFile(this.tableSchema.getTableName(), "rw", true);
		this.channel = raf.getChannel();
		this.elementsPositions = this.instantiateElementsPositions();
	}
	
	/**
	 * Get all positions where objects are matched by given keys.
	 */
	public List<Integer> getPositionsForKeys(ByteBuffer keys, int numberKeys){
		return null;
	}
	
	/**
	 * Delete one element. Note that it's not really deleted, just freed place.
	 */
	public boolean deleteElement(ByteBuffer keys, int numberKeys){
		return false;
	}
	
	/**
	 * Get the position of the table where to write this object
	 */
	public int insertElement(ByteBuffer object) throws Exception{
		int whereToWrite = (elementsPositions.size()+1)*this.ELEMENT_SIZE;
		int position = this.getPositionAndInsert(object, whereToWrite);
		this.shiftData(position, this.KEYS_SIZE);
		this.writeToData(this.createKeysFromByteBufferAndPosition(object, whereToWrite).array(), position);
		object.rewind();
		return whereToWrite;
	}
	
	////
	//Private methods
	////
	
	private ByteBuffer createKeysFromByteBufferAndPosition(ByteBuffer object, int positionToWrite){
		ByteBuffer ret = ByteBuffer.allocate(this.KEYS_SIZE+4);
		ret.putInt(1);
		for (int i = 0; i < this.KEYS_SIZE-4; i++){
			ret.put(object.get());
		}
		ret.putInt(positionToWrite);
		ret.rewind();
		object.rewind();
		return ret;
	}
	
	
	
	private void shiftData(int position, int numberBytes){
		return;
	}
	
	private ByteBuffer getKeysFromByteBuffer(ByteBuffer object){
		ByteBuffer ret = ByteBuffer.allocate(this.KEYS_SIZE);
		for (int i = 0; i < this.KEYS_SIZE-4;i++)
			ret.put(object.get());
		ret.rewind();
		object.rewind();
		return ret;
	}
	
	private int getPositionAndInsert(ByteBuffer object, int whereToWrite) throws Exception{
		int i = 0;
		ByteBuffer keyToInsert = this.getKeysFromByteBuffer(object);
		while((i < elementsPositions.size()) && serializer.compareKeys(keyToInsert, ByteBuffer.wrap(elementsPositions.get(i).first), this.tableSchema))
				i++;
		elementsPositions.add(i, new Pair<byte[], Integer>(serializer.serialize(object, this.tableSchema), whereToWrite));
		return 1024+i*this.KEYS_SIZE;
	}
	
	private List<Pair<byte[], Integer>> instantiateElementsPositions() throws IOException{
		List<Pair<byte[], Integer>> elementsPositions = new ArrayList<Pair<byte[], Integer>>();
		MappedByteBuffer buf = this.channel.map(FileChannel.MapMode.READ_WRITE, this.OFFSET_META_DATA, this.MAXIMAL_META_DATA_SIZE);
		byte[] bytes = new byte[buf.capacity()];
		buf.get(bytes);
		int i = 0;
		while ((int)bytes[i+3] == 1){
			System.out.print("I'm in!");
			elementsPositions.add(new Pair<byte[], Integer>(Arrays.copyOfRange(bytes, i+4, i+4+this.KEYS_SIZE), i*this.ELEMENT_SIZE));
			i += this.KEYS_SIZE;
		}
		return elementsPositions;
	}
}
