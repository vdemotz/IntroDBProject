package ch.ethz.inf.dbproject.sqlRevisited;

import java.nio.ByteBuffer;
<<<<<<< HEAD
import java.nio.charset.Charset;
=======
import java.nio.CharBuffer;
>>>>>>> alternatePhysicalInterface
import java.util.ArrayList;
import java.util.List;

import ch.ethz.inf.dbproject.sqlRevisited.Codegen.Materializer;
import ch.ethz.inf.dbproject.sqlRevisited.SQLType.BaseType;

public class Serializer {

	
		public Serializer(){
			
		}
		
		////
		//Utilities
		////
		
		/**
		 * Get a int from a byte array
		 */
		public int getIntFromByteArray(byte[] bytes, int offset) {
		     return (int)bytes[offset] << 24 | ((int)bytes[offset + 1] & 0xFF) << 16 | ((int)bytes[offset + 2] & 0xFF) << 8 | ((int)bytes[offset + 3] & 0xFF);
		}
		
		/**
		 * Compare two keys. Be aware of positions of pointer of key1 and key2
		 * @param numberKeys number of keys compared
		 * @return true if key1 < key2, false otherwise
		 */
		private boolean compareKeys(ByteBuffer key1, ByteBuffer key2, SQLType type) throws Exception{
			int position1 = key1.position();
			int position2 = key2.position();
			boolean ret;
			if (type.type == SQLType.BaseType.Integer){
				ret = (key1.getInt() < key2.getInt()) ? true : false;
			} else if (type.type == SQLType.BaseType.Varchar){
				//O.o Long line
				ret = (this.getStringFromByteBuffer(key1).compareTo(this.getStringFromByteBuffer(key2)) < 0) ? true : false;
			} else {
				throw new Exception("Key type not supported " + type.toString());
			}
			key1.position(position1);
			key2.position(position2);
			return ret;
		}
		
		private boolean compareEqualityKeys(ByteBuffer key1, ByteBuffer key2, SQLType type) throws Exception{
			int position1 = key1.position();
			int position2 = key2.position();
			boolean ret;
			if (type.type == SQLType.BaseType.Integer){
				ret = (key1.getInt() == key2.getInt()) ? true : false;
			} else if (type.type == SQLType.BaseType.Varchar){
				//O.o Long line
				ret = (this.getStringFromByteBuffer(key1).compareTo(this.getStringFromByteBuffer(key2)) == 0) ? true : false;
			} else {
				throw new Exception("Key type not supported " + type.toString());
			}
			key1.position(position1);
			key2.position(position2);
			return ret;
		}
		
		/**
		 * Compare equality of two keys of a table, based on the position of the bytebuffer and the tableSchema.
		 * True if key1 == key2, false otherwise
		 */
		public boolean compareEqualityKeys(ByteBuffer key1, ByteBuffer key2, TableSchema tableSchema) throws Exception{
			int position1 = key1.position();
			int position2 = key2.position();
			boolean ret = false;
			if (tableSchema.getKeys().length == 1){
				ret = this.compareEqualityKeys(key1, key2, tableSchema.getKeys()[0]);
			} else{
				ret = this.compareEqualityKeys(key1, key2, tableSchema.getKeys()[0]);
				key1.position(position1+tableSchema.getKeys()[0].byteSizeOfType());
				key2.position(position2+tableSchema.getKeys()[0].byteSizeOfType());
				ret = ret && this.compareEqualityKeys(key1, key2, tableSchema.getKeys()[1]);
			}
			key1.position(position1);
			key2.position(position2);
			return ret;
		}

		/**
		 * Compare two keys of a table, based on the position of the bytebuffer and the tableSchema.
		 * True if key1 < key2, false otherwise
		 */
		public boolean compareKeys(ByteBuffer key1, ByteBuffer key2, TableSchema tableSchema) throws Exception{
			int position1 = key1.position();
			int position2 = key2.position();
			boolean ret = false;
			if (tableSchema.getKeys().length == 1){
				ret = this.compareKeys(key1, key2, tableSchema.getKeys()[0]);
			} else{
				boolean firstKey = this.compareKeys(key1, key2, tableSchema.getKeys()[0]);
				if (firstKey){
					return true;
				} else {
					key1.position(position1+tableSchema.getKeys()[0].byteSizeOfType());
					key2.position(position2+tableSchema.getKeys()[0].byteSizeOfType());
					boolean firstKeyEq = compareEqualityKeys(key1, key2, tableSchema.getKeys()[1]);
					if (firstKeyEq)
						ret = compareKeys(key1, key2, tableSchema.getKeys()[1]);
					else
						ret = false;
				}
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
		 * Create a new object of the class given by table name and written in ByteBuffer
		 * @param data represents an object
		 * @param tableName the type of the object
		 * @return an object of type tableName.class
		 */
		public static Object createObjectFromByteBufferAndTableName(ByteBuffer data, String tableName){
			return null;
		}
		
		/**
		 * Create a new object of the class given by table name and written in byte array
		 * @param data represents an object
		 * @param tableName the type of the object
		 * @return an object of type tableName.class
		 */
		public static Object createObjectFromBytesArrayAndTableName(byte[] data, SQLType[] attributesTypes) throws Exception{
			List<Object> attributes = new ArrayList<Object>();
			for (int i = 0; i < attributesTypes.length; i++){
				
			}
			return null;
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
				destination.put(result);
			} else {
				throw new SQLPhysicalException();
			}
		}
		
		public static void putBytesFromTuple(TableSchema schema, ByteBuffer destination, Object... data) throws SQLPhysicalException 
		{
			for (int i=0; i<schema.getLength(); i++) {
				putBytes(data[i], schema.getAttributesTypes()[i], destination);
			}
		}
		
		/**
		 * Get a String from a ByteBuffer
		 * @param data a ByteBuffer which represents a String
		 * @param length number of character to read
		 * @return a string
		 */
		public String getStringFromByteBuffer(ByteBuffer data){
			int position = data.position();
			String ret = "";
			int length = data.getInt();
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
			String ret = "";
			int length = (int)data[3]+(int)data[2]*16;
			for (int i = 4; i < length+4; i++){
				ret = ret+(char)data[i];
			}
			return ret;
		}
		
		/**
		 * @param data a byte[] that contains a serialization of a varchar, starting at the startIndex (inclusive)
		 * @param startIndex the index of the first byte belonging to the varchar
		 * @return a string representing the Varchar
		 */
		public static String getVarcharFromByteArray(byte[] data, int startIndex) {
			return new String(data);
		}
		
		public static String getCharFromBuffer(ByteBuffer data, int length) {
			CharBuffer charbuf = data.asCharBuffer();
			char[] result = new char[length];
			charbuf.get(result);
			return new String(result);
		}
		
}
