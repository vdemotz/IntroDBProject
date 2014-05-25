package ch.ethz.inf.dbproject.sqlRevisited.Codegen;

class PredicateFromNaturalComparison<T extends Comparable<T>> implements PredicateFromComparison<T> {

	public final boolean allowLess;
	public final boolean allowEqual;
	public final boolean allowGreater;
	
	PredicateFromNaturalComparison(boolean allowLess, boolean allowEqual, boolean allowGreater) {
		this.allowEqual = allowEqual;
		this.allowGreater = allowGreater;
		this.allowLess = allowLess;
	}
	
	public boolean has(T left, T right)
	{
		int comparison = ((Comparable<T>) left).compareTo(right);
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
