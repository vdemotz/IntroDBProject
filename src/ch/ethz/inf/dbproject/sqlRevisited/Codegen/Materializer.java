package ch.ethz.inf.dbproject.sqlRevisited.Codegen;

interface Materializer<T> {
	
	T get(byte[] bytes);

}
