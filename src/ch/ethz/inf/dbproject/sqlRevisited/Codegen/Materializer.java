package ch.ethz.inf.dbproject.sqlRevisited.Codegen;

public interface Materializer<T> {
	
	T get(byte[] bytes);

}
