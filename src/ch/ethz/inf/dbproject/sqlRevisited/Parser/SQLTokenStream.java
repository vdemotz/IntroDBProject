package ch.ethz.inf.dbproject.sqlRevisited.Parser;

import java.util.ArrayList;

/**
 * Represents a list of tokens that can be read sequentially
 * Conceptually the list is extended with null to be infinitely long, as to never cause out of bounds exceptions, but instead returning null
 */
public class SQLTokenStream {
	
	private final ArrayList<SQLToken> items;
	private int position;
	
	/**
	 * @param items the list backing this token stream
	 */
	public SQLTokenStream(ArrayList<SQLToken> items) {
		this.items = items;
		rewind();
	}
	
	/**
	 * @return the current token in the stream, or null if all tokens have been read
	 */
	public SQLToken getToken() {
		if (position >= items.size()) {
			return null;
		}
		return items.get(position);
	}
	
	/**
	 * @return the class of the current token in the stream, of null if all tokens have been read
	 */
	public SQLToken.SQLTokenClass getTokenClass() {
		if (position >= items.size()) {
			return null;
		}
		return items.get(position).tokenClass;
	}
	
	public int getPosition() {
		return position;
	}
	
	/**
	 * Move to the next token if there are still tokens to read, or do nothing otherwise
	 */
	public void advance() {
		position += 1;
	}
	
	/**
	 * Go back to the beginning of the stream
	 */
	public void rewind() {
		position = 0;
	}
	
	public String toString()
	{
		return "{ " +  position + ", " + this.items.toString() + " }";
	}
}
