package ch.ethz.inf.dbproject.sqlRevisited.Codegen;

import java.nio.ByteBuffer;

public interface Predicate<T> {

	public boolean has(T nextResult);

}
