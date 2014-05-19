package ch.ethz.inf.dbproject.sqlRevisited.Codegen;

import java.util.Comparator;


public class MaterializingPredicate <T extends Comparable<T>> implements Predicate<byte[]> {

	public final PredicateFromComparison predicate;
	public final Materializer<T> materializerLeft;
	public final Materializer<T> materializerRight;
	
	MaterializingPredicate(Materializer<T> leftMaterializer, Materializer<T> rightMaterializer, PredicateFromComparison predicate) {
		this.predicate = predicate;
		this.materializerLeft = leftMaterializer;
		this.materializerRight = rightMaterializer;
	}
	
	@Override
	public boolean has(byte[] nextResult) {
		return predicate.compare(materializerLeft.get(nextResult), materializerRight.get(nextResult));
	}
	
}
