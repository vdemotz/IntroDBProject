package ch.ethz.inf.dbproject.sqlRevisited;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import ch.ethz.inf.dbproject.sqlRevisited.SQLType.BaseType;

public class Serializer {

	
		public Serializer(){
			
		}
		
		////
		//Utilities
		////
		
		/**
		 * Compare two keys. Be aware of positions of pointer of key1 and key2, the method restore the position of
		 * key1 and key2
		 * @param numberKeys number of keys compared
		 * @return negative if key1 < key2, positive if key2 > key1, 0 otherwise
		 */
		private int compareKeys(ByteBuffer key1, ByteBuffer key2, SQLType type) throws Exception{
			int position1 = key1.position();
			int position2 = key2.position();
			int ret = 0;
			if (type.type == SQLType.BaseType.Integer){
				int key1I = key1.getInt();
				int key2I = key2.getInt();
				ret = key1I - key2I;
			} else if (type.type == SQLType.BaseType.Varchar){
				String key1S = getStringFromByteBuffer(key1);
				String key2S = getStringFromByteBuffer(key2);
				ret = key1S.compareTo(key2S);
			} else {
				throw new Exception("Key type not supported " + type.toString());
			}
			key1.position(position1);
			key2.position(position2);
			return ret;
		}

		/**
		 * Compare two keys of a table, based on the position of the ByteBuffer and the tableSchema.
		 * True if key1 < key2, false otherwise
		 */
		public int compareKeys(ByteBuffer key1, ByteBuffer key2, TableSchema tableSchema) throws Exception{
			int position1 = key1.position();
			int position2 = key2.position();
			int ret = 0;
			int i = 0;
			while (tableSchema.getIfPrimaryKey().length > i && tableSchema.getIfPrimaryKey()[i]){
				//System.out.println("Compare key");
				ret = this.compareKeys(key1, key2, tableSchema.getAttributesTypes()[i]);
				if (ret != 0)
					break;
				i++;
			}
			
			key1.position(position1);
			key2.position(position2);
			return ret;
		}
		
		/**
		 * Read an array of String and turns it into a boolean array
		 * @param array of String representing booleans
		 * @return an array of boolean
		 * @throws Exception
		 */
		public static boolean[] getBooleanArrayFromStringArray(String[] array) throws Exception{
			boolean[] ret = new boolean[array.length];
			for (int i = 0; i < array.length; i++){
				if (array[i].equals("1")) { ret[i] = true; }
				else if (array[i].equals("0")) { ret[i] = false; }
				else { throw new Exception("The String " + array[i] + " doesn't represent a boolean"); } 
			}
			return ret;
		}
			
		/**
		 * Read an array of String and turns it into a SQLType array
		 * @param array of String representing SQLType
		 * @return an array of SQLTypes
		 * @throws Exception
		 */
		public static SQLType[] getSQLTypeArrayFromStringArray(String[] array) throws Exception{
			SQLType[] ret = new SQLType[array.length];
			for (int i = 0; i < array.length; i++){
				ret[i] = getSQLTypeFromString(array[i]);
			}
			return ret;
		}
		
		/**
		 * Return a SQLType from a String
		 * @param type represents a SQLType of the form SQLType.BaseType,SIZE
		 * @return a new SQLType
		 * @throws Exception
		 */
		public static SQLType getSQLTypeFromString(String type) throws Exception{
			String[] splitted = type.split(",");
			Integer length = 0;
			try {	
				length = Integer.parseInt(splitted[1]);
			} catch (Exception ex){
				length = null;
			}
			
			if (splitted[0].equals("Integer")){
				return new SQLType(SQLType.BaseType.Integer, length);
			} else if (splitted[0].equals("Char")){
				return new SQLType(SQLType.BaseType.Char, length);
			} else if (splitted[0].equals("Varchar")){
				return new SQLType(SQLType.BaseType.Varchar, length);
			} else if (splitted[0].equals("Date")){
				return new SQLType(SQLType.BaseType.Date, length);
			} else if (splitted[0].equals("Datetime")){
				return new SQLType(SQLType.BaseType.Datetime, length);
			} else if (splitted[0].equals("Boolean")){
				return new SQLType(SQLType.BaseType.Boolean, length);
			} else {
				throw new Exception("The String "+type+" doesn't represent a SQLType");
			}
		}
		
		/**
		 * Get an array of object that were represented in data, interpreted regarding schema provided
		 * @param data an abstract array of bytes representing an array of object
		 * @param schema a way to interpret data
		 * @return array of object interpreted
		 */
		public static Object[] getObjectsFromBytes(byte[] data, TableSchema schema) {
			SQLType[] attributeTypes = schema.getAttributesTypes();
			Object[] result = new Object[schema.getLength()];
			for (int i = 0; i < attributeTypes.length; i++){
				byte[] copyOfRange = Arrays.copyOfRange(data, schema.getSizeOfAttributes(i), schema.getSizeOfEntry());
				if (attributeTypes[i].type == SQLType.BaseType.Varchar || attributeTypes[i].type == SQLType.BaseType.Date || attributeTypes[i].type == SQLType.BaseType.Datetime) {
					result[i] = Serializer.getStringFromByteArray(copyOfRange);
				} else if (attributeTypes[i].type == SQLType.BaseType.Integer) {
					result[i] = Serializer.getIntegerFromByteArray(copyOfRange);
				} else if (attributeTypes[i].type == SQLType.BaseType.Boolean) {
					result[i] = Serializer.getBooleanFromByteArray(copyOfRange);
				}
			}
			return result;
		}

		/**
		 * Transform an object into a byte array to write into the database
		 * @param data the object to transform
		 * @return a byte array to write to database
		 */
		public static byte[] getByteArrayFromObject(Object data, SQLType type) throws Exception{
			byte[] ret = null;
			if(type.type == BaseType.Integer){
				ret = ByteBuffer.allocate(type.byteSizeOfType()).putInt((int) data).array();
			} else if (type.type == BaseType.Boolean){
				boolean dataB = (boolean) data;
				ret = dataB ? "1".getBytes() : "0".getBytes();
			} else if (type.type == BaseType.Char){
				ret = ByteBuffer.allocate(type.byteSizeOfType()).putChar((char) data).array();
			} else if (type.type == BaseType.Varchar || type.type == BaseType.Date || type.type == BaseType.Datetime){
				byte[] c = new byte[((String)data).length()+4];
				byte[] size = getByteArrayFromObject(((String)data).length(), new SQLType(BaseType.Integer));
				c = ((String)data).getBytes(Charset.defaultCharset());
				ret = new byte[size.length + c.length];
				System.arraycopy(size, 0, ret, 0, size.length);
				System.arraycopy(c, 0, ret, size.length, c.length);
			} else {
				throw new Exception("Not accepted SQLType");
			}
			return ret;
		}
		
		/**
		 * Transform an object into a byte array to write into the database
		 * @param data the object to transform
		 * @return a byte array to write to database
		 */
		public static void putBytes(Object data, SQLType type, ByteBuffer destination) throws SQLPhysicalException {
			if(type.type == BaseType.Integer){
				destination.putInt((int)data);
			} else if (type.type == BaseType.Boolean) {
				boolean isTrue = (boolean)data;
				if (isTrue) {
					destination.put("1".getBytes());
				} else {
					destination.put("0".getBytes());
				}
			} else if (type.type == BaseType.Char){
				destination.putChar((char) data);
			} else if (type.type == BaseType.Varchar || type.type == BaseType.Date || type.type == BaseType.Datetime){
				byte[] result = ((String)data).getBytes();
				putBytes(result.length, new SQLType(BaseType.Integer), destination);
				destination.put(result);
			} else {
				throw new SQLPhysicalException();
			}
		}
		
		/**
		 * Get a boolean from a byte array
		 */
		public static Boolean getBooleanFromByteArray(byte[] copyOfRange) {
			String asString = new String(Arrays.copyOfRange(copyOfRange, 0, SQLType.CHARACTER_BYTE_SIZE));
			if (asString.startsWith("1")) {
				return true;
			} else {
				return false;
			}
		}
		
		/**
		 * Get a boolean from a ByteBuffer
		 */
		public static Boolean getBooleanFromByteBuffer(ByteBuffer buf){
			int position = buf.position();
			char a = (char)buf.get();
			boolean ret = false;
			if (a == '1')
				ret = true;
			else
				ret = false;
			buf.position(position);
			return ret;
		}
		
		/**
		 * Put bytes from a tuple into a ByteBuffer
		 * @param schema a way to interpret data
		 * @param destination ByteBuffer to write data
		 * @param data object
		 * @throws SQLPhysicalException
		 */
		public static void putBytesFromTuple(TableSchema schema, ByteBuffer destination, Object... data) throws SQLPhysicalException 
		{
			for (int i=0; i<schema.getLength(); i++) {
				destination.mark();
				putBytes(data[i], schema.getAttributesTypes()[i], destination);
				destination.reset();
				destination.position(destination.position()+schema.getAttributesTypes()[i].byteSizeOfType());
			}
		}
		
		/**
		 * Get a String from a ByteBuffer
		 * @param data a ByteBuffer which represents a String
		 * @param length number of character to read
		 * @return a string
		 */
		public static String getStringFromByteBuffer(ByteBuffer data){
			int position = data.position();
			String ret = "";
			int length = data.getInt();
			//System.out.println("Serialize with length :"+length);
			for (int i = 0; i < length; i++){
				char a = (char)data.get();
				ret = ret+a;
			}
			data.position(position);
			return ret;
		}
		
		/**
		 * Get a String from a byte array. (Represents Varchar) The four first entries represent an integer which is the length of the string.
		 * @param data a byte[] which represents a String
		 * @return a string
		 */
		public static String getStringFromByteArray(byte[] data){
			int length = (int)data[3]+(int)data[2]*16;
			return new String(Arrays.copyOfRange(data, 4, 4+length));
		}
		
		/**
		 * Get first integer from a byte buffer.
		 */
		@Deprecated
		public static Integer getIntegerFromByteBuffer(ByteBuffer data) {
			return data.getInt();
		}
		
		/**
		 * Get the first integer represented in a byte array
		 * @param data a byte array of length at least 4, where 4 first entries represents an integer
		 * @return an integer
		 */
		public static Integer getIntegerFromByteArray(byte[] data) {
			ByteBuffer wrap = ByteBuffer.wrap(data);
			return wrap.getInt();
		}
		
		/**
		 * Serialize a string, given a max length
		 */
		public static ByteBuffer serializerVarchar(String s, int l){
			ByteBuffer buf = ByteBuffer.allocate(l);
			if (s == null){
				buf.putInt(0);
			} else {
				int length = s.length();
				buf.putInt(length);
				buf.put(s.getBytes());
			}
			buf.rewind();
			return buf;
		}
		
		/**
		 * Serialize a boolean
		 */
		public static ByteBuffer serializerBoolean(boolean b){
			ByteBuffer buf = ByteBuffer.allocate(1);
			if (b)
				buf.put("1".getBytes());
			else 
				buf.put("0".getBytes());
			buf.rewind();
			return buf;
		}
		
		/**
		 * Serialize a character
		 */
		public static ByteBuffer serializerCharacter(char c){
			ByteBuffer buf = ByteBuffer.allocate(1);
			buf.put((byte)c);
			buf.rewind();
			return buf;
		}
		
		/**
		 * Serialize an object regarding its type
		 * @param object an object to serialize
		 * @param type how to serialize
		 * @return a ByteBuffer containing the object, set to position 0
		 */
		public static ByteBuffer serializerObject(Object object, SQLType type){
			ByteBuffer buf = ByteBuffer.allocate(type.byteSizeOfType());
			if(type.type == BaseType.Integer){
				buf.putInt((int)object);
			} else if (type.type == BaseType.Boolean){
				buf = serializerBoolean((boolean)object);
			} else if (type.type == BaseType.Char){
				buf = serializerCharacter((char)object);
			} else if (type.type == BaseType.Varchar || type.type == BaseType.Date || type.type == BaseType.Datetime){
				buf = serializerVarchar((String)object, type.byteSizeOfType());
			}  
			return (ByteBuffer)buf.rewind();
		}
		
		/**
		 * Serialize an entire tuple stored in an array of objects
		 * @param tableSchema serialize regarding types
		 * @param objects an array containing objects to serialize
		 * @return a ByteBuffer representing objects, set to position 0
		 */
		public static ByteBuffer serializerTuple(TableSchema tableSchema, Object[] objects){
			ByteBuffer buf = ByteBuffer.allocate(tableSchema.getSizeOfEntry());
			for (int i = 0; i < objects.length; i++){
				buf.put(serializerObject(objects[i], tableSchema.getAttributesTypes()[i]));
			}
			return (ByteBuffer)buf.rewind();
		}
}
