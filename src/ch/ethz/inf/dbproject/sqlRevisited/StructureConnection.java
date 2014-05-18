package ch.ethz.inf.dbproject.sqlRevisited;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
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
		this.ELEMENT_SIZE = tableSchema.getSizeOfEntry();
		this.KEYS_SIZE = tableSchema.getSizeOfKeys();
		this.tableSchema = tableSchema;
		this.raf = this.getRandomAccesFile(this.tableSchema.getTableName(), "rw", true);
		this.channel = raf.getChannel();
		this.elementsPositions = this.instantiateElementsPositions();
		this.serializer = new Serializer();
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
		int i = 0;
		while (i < elementsPositions.size()){
			if (serializer.compareKeys(object, ByteBuffer.wrap(elementsPositions.get(i).first), this.tableSchema.getKeys()[0]))
				break;
			i++;
		}
		this.writeToData(object.array(), 0);
		return elementsPositions.size()*this.ELEMENT_SIZE;
	}
	
	private List<Pair<byte[], Integer>> instantiateElementsPositions() throws IOException{
		List<Pair<byte[], Integer>> elementsPositions = new ArrayList<Pair<byte[], Integer>>();
		MappedByteBuffer buf = this.channel.map(FileChannel.MapMode.READ_WRITE, this.OFFSET_META_DATA, this.MAXIMAL_META_DATA_SIZE);
		buf.flip();
		byte[] bytes = new byte[buf.remaining()];
		buf.get(bytes);
		assert(bytes.length % this.KEYS_SIZE == 0);
		System.out.println("Capacity of the buffer : "+buf.capacity()+" length bytes : "+bytes.length);
		return elementsPositions;
	}
}
