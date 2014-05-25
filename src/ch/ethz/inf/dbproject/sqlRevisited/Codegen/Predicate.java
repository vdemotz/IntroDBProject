package ch.ethz.inf.dbproject.sqlRevisited.Codegen;

import java.nio.ByteBuffer;

interface Predicate<T> {

	public boolean has(T nextResult);

}
