package ch.ethz.inf.dbproject.sqlRevisited;

import java.nio.ByteBuffer;
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
				ret = ((String)data).getBytes();
			} else {
				throw new Exception("Not accepted SQLType");
			}
			return ret;
		}
		
		/**
		 * Get a String from a ByteBuffer
		 * @param data a ByteBuffer which represents a String
		 * @param length number of character to read
		 * @return a string
		 */
		public static String getStringFromByteBuffer(ByteBuffer data, int length){
			String ret = "";
			for (int i = 0; i < length; i++){
				char a = data.getChar();
				System.out.print(i+a);
				ret = ret+a;
			}
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
		public static String getVarcharFromByteArray(byte[] data, int startIndex){
			String ret = "";
			int length = (int)data[3+startIndex]+(int)data[2+startIndex]*16;
			for (int i = 4+startIndex; i < length+4+startIndex; i++){
				ret = ret+(char)data[i];
			}
			return ret;
		}
		
}
