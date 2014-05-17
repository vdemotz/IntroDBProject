package ch.ethz.inf.dbproject.sqlRevisited;

import java.nio.ByteBuffer;
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
	public int insertElement(ByteBuffer object){
		int i = 0;
		while (i < elementsPositions.size()){
			
		}
		return elementsPositions.size()*this.ELEMENT_SIZE;
	}
	
	private List<Pair<byte[], Integer>> instantiateElementsPositions(){
		List<Pair<byte[], Integer>> elementsPositions = new ArrayList<Pair<byte[], Integer>>();
		return elementsPositions;
	}
}
