package ch.ethz.inf.dbproject.sqlRevisited;

import java.nio.ByteBuffer;

public interface Predicate<T> {

	public boolean has(T nextResult);

}
