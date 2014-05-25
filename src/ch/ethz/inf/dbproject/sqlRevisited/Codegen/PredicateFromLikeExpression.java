package ch.ethz.inf.dbproject.sqlRevisited.Codegen;

class PredicateFromLikeExpression implements PredicateFromComparison<String> {

	@Override
	public boolean has(String left, String right) {
		String formattedRight = right.replace("%", "(.)*");
		return left.matches(formattedRight);
	}

}
