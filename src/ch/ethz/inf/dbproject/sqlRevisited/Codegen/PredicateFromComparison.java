package ch.ethz.inf.dbproject.sqlRevisited.Codegen;

import java.util.Comparator;

public interface PredicateFromComparison<T> {
	
	public boolean has(T left, T right);
	
}
