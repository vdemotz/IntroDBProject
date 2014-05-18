package ch.ethz.inf.dbproject.sqlRevisited.Codegen;

import java.util.Comparator;


public class MaterializingPredicate <T extends Comparable<T>> implements Predicate<byte[]> {

	PredicateFromComparison predicate;
	public Materializer<T> materializerLeft;
	public Materializer<T> materializerRight;
	
	MaterializingPredicate(Materializer<T> leftMaterializer, Materializer<T> rightMaterializer, PredicateFromComparison predicate) {
		this.predicate = predicate;
	}
	
	@Override
	public boolean has(byte[] nextResult) {
		return predicate.compare(materializerLeft.get(nextResult), materializerRight.get(nextResult));
	}
	
}
