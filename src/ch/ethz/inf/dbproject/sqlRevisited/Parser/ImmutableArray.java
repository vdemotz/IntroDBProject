package ch.ethz.inf.dbproject.sqlRevisited.Parser;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ImmutableArray<T> implements Iterable<T>{


	public final int length;
	private final T[] elements;
	
	/**
	 * If the elements that are passed in are immutable, then so is an instance of ImmutableArray.
	 * In any case, instances are unmodifiable.
	 * Changes to the constructing array are not reflected in the instance, as a local copy is made
	 */
	public ImmutableArray(T[] elements)
	{
		this.length = elements.length;
		this.elements = elements.clone();
	}
	
	public T get(int i)
	{
		return elements[i];
	}
	
	public List<T> getAsUnmodifiableList()
	{
		return Collections.unmodifiableList(Arrays.asList(elements));
	}

	@Override
	public Iterator<T> iterator() {
		return getAsUnmodifiableList().iterator();
	}
	
}
