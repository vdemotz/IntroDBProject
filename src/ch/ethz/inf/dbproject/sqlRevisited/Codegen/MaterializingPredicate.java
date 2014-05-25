package ch.ethz.inf.dbproject.sqlRevisited.Codegen;

import java.util.Comparator;


class MaterializingPredicate <T extends Comparable<T>> implements Predicate<byte[]> {

	public final PredicateFromComparison<T> comparator;
	public final Materializer<T> materializerLeft;
	public final Materializer<T> materializerRight;
	
	/**
	 * The predicate is true if the comparator returns 0
	 */
	MaterializingPredicate(Materializer<T> leftMaterializer, Materializer<T> rightMaterializer, PredicateFromComparison<T> comparator) {
		this.comparator = comparator;
		this.materializerLeft = leftMaterializer;
		this.materializerRight = rightMaterializer;
	}
	
	@Override
	public boolean has(byte[] nextResult) {
		return comparator.has(materializerLeft.get(nextResult), materializerRight.get(nextResult));
	}
	
}
