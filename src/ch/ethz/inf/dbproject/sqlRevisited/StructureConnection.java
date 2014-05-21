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
	
	
	
	@Override
	protected void finalize() throws Throwable{
		channel.close();
		raf.close();
		super.finalize();
	}
	
	/**
	 * Get tuple matched by keys
	 */
	public int getPositionsForKeys(ByteBuffer keys) throws Exception{
		for (int i = 0; i < elementsPositions.size(); i++){
			if (serializer.compareEqualityKeys(keys, ByteBuffer.wrap(elementsPositions.get(i).first), this.tableSchema))
				return elementsPositions.get(i).second;
		}
		return -1;
	}
	
	/**
	 * Get tuple matched by keys
	 */
	public int getPositionsNextForKeys(ByteBuffer keys) throws Exception{
		for (int i = 0; i < elementsPositions.size(); i++){
			if (serializer.compareEqualityKeys(keys, ByteBuffer.wrap(elementsPositions.get(i).first), this.tableSchema))
				if (i+1 < elementsPositions.size())
					return elementsPositions.get(i+1).second;
				else
					break;
		}
		return -1;
	}
	
	/**
	 * Delete one element. Note that it's not really deleted, just freed place.
	 */
	public boolean deleteElement(ByteBuffer keys) throws Exception{
		int position = -1;
		for (int i = 0; i < elementsPositions.size(); i++){
			if (serializer.compareEqualityKeys(keys, ByteBuffer.wrap(elementsPositions.get(i).first), this.tableSchema))
				position = i;
				break;
		}
		if (position == -1){
			System.err.println("Key not found");
			return false;
		}
		this.shiftData((this.KEYS_SIZE+8)*position+this.OFFSET_META_DATA, -(8+this.KEYS_SIZE));
		elementsPositions.remove(position);
		return true;
	}
	
	/**
	 * Get the position of the table where to write this object
	 */
	public int insertElement(ByteBuffer object) throws Exception{
		int whereToWrite = (elementsPositions.size()+1)*this.ELEMENT_SIZE;
		int position = this.getPositionAndInsert(object, whereToWrite);
		//System.out.println("Returned position : "+position);
		this.shiftData(position, this.KEYS_SIZE+8);
		this.writeToData(this.createKeysFromByteBufferAndPosition(object, whereToWrite).array(), position);
		object.rewind();
		return whereToWrite;
	}
	
	////
	//Private methods
	////
	
	private ByteBuffer createKeysFromByteBufferAndPosition(ByteBuffer object, int positionToWrite){
		ByteBuffer ret = ByteBuffer.allocate(this.KEYS_SIZE+8);
		ret.putInt(1);
		for (int i = 0; i < this.KEYS_SIZE; i++){
			ret.put(object.get());
		}
		ret.putInt(positionToWrite);
		ret.rewind();
		object.rewind();
		//System.out.println("Number bytes wrote : "+ret.remaining());
		return ret;
	}
	
	
	
	private void shiftData(int position, int numberBytes) throws IOException{
		ByteBuffer buf = ByteBuffer.allocate(MAXIMAL_META_DATA_SIZE);
		buf.rewind();
		int a = channel.read(buf, position);
		//System.out.println("Bytes read : "+a);
		buf.rewind();
		//System.out.println("Wrote at : "+(position+numberBytes));
		a = channel.write(buf, position+numberBytes);
		//System.out.println("Bytes wrote : "+a);
		return;
	}
	
	private ByteBuffer getKeysFromByteBuffer(ByteBuffer object){
		ByteBuffer ret = ByteBuffer.allocate(this.KEYS_SIZE);
		for (int i = 0; i < this.KEYS_SIZE-8;i++)
			ret.put(object.get());
		ret.rewind();
		object.rewind();
		return ret;
	}
	
	private int getPositionAndInsert(ByteBuffer object, int whereToWrite) throws Exception{
		int i = 0;
		ByteBuffer keyToInsert = this.getKeysFromByteBuffer(object);
		while((i < elementsPositions.size()) && !(serializer.compareKeys(keyToInsert, ByteBuffer.wrap(elementsPositions.get(i).first), this.tableSchema))){
			//System.out.println("Enter loop search");	
			i++;
		}
		elementsPositions.add(i, new Pair<byte[], Integer>(keyToInsert.array(), whereToWrite));
		for (int k = 0; k < elementsPositions.size(); k++){
			//System.out.println("Fresh Key : "+Serializer.getStringFromByteArray(elementsPositions.get(k).first));
		}
		return 1024+i*(this.KEYS_SIZE+8);
	}
	
	private List<Pair<byte[], Integer>> instantiateElementsPositions() throws IOException{
		List<Pair<byte[], Integer>> elementsPositions = new ArrayList<Pair<byte[], Integer>>();
		MappedByteBuffer buf = this.channel.map(FileChannel.MapMode.READ_WRITE, this.OFFSET_META_DATA, this.MAXIMAL_META_DATA_SIZE);
		byte[] bytes = new byte[buf.capacity()];
		buf.get(bytes);
		int i = 0;
		while ((int)bytes[i+3] == 1){
			elementsPositions.add(new Pair<byte[], Integer>(Arrays.copyOfRange(bytes, i+4, i+4+this.KEYS_SIZE), elementsPositions.size()*this.ELEMENT_SIZE));
			i += this.KEYS_SIZE+8;
			//System.out.println("Keys at position "+i);
		}
		for (int k = 0; k < elementsPositions.size(); k++){
			//System.out.println("Key : "+Serializer.getStringFromByteArray(elementsPositions.get(k).first)+" : "+elementsPositions.get(k).second);
		}
		return elementsPositions;
	}



	@Override
	public TableSchema getTableSchema() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean delete(ByteBuffer key) throws SQLPhysicalException {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean insert(ByteBuffer value) throws SQLPhysicalException {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public TableIterator getIterator(ByteBuffer key)
			throws SQLPhysicalException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TableIterator getIteratorFirst() throws SQLPhysicalException {
		// TODO Auto-generated method stub
		return null;
	}
}
