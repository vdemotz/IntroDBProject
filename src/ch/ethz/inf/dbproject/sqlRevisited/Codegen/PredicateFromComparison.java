package ch.ethz.inf.dbproject.sqlRevisited.Codegen;

import java.util.Comparator;

public class PredicateFromComparison {

	public final boolean allowLess;
	public final boolean allowEqual;
	public final boolean allowGreater;
	
	PredicateFromComparison(boolean allowLess, boolean allowEqual, boolean allowGreater)
	{
		this.allowEqual = allowEqual;
		this.allowGreater = allowGreater;
		this.allowLess = allowLess;
	}
	
	protected <T> boolean compare(Comparable<T> left, T right)
	{
		int comparison = left.compareTo(right);
		boolean result = false;
		if (allowLess) {
			result = comparison < 0;
		}
		if (allowEqual) {
			result = result | comparison == 0;
		}
		if (allowGreater) {
			result = result | comparison > 0;
		}
		return result;
	}
	
}
