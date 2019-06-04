package com.github.dusby.predicates;

public class PathPredicate extends PredicateAggregate {

	public static final String SYMBOL = " /\\ ";

	public PathPredicate() {
		super();
	}

	public PathPredicate(PredicateProvider predicate) {
		super(predicate);
	}

	public PathPredicate(PathPredicate cp) {
		super();
		for(PredicateProvider p : cp.predicates) {
			this.predicates.add(new Predicate((Predicate) p));
		}
	}

	@Override
	public String getSymbol() {
		return SYMBOL;
	}

	@Override
	protected String getAuthorizedClassMessage() {
		return "PathPredicate class only accepts predicates of Predicate class (P0 /\\ ... /\\ Pn)";
	}

	@Override
	protected Class<? extends PredicateProvider> getAuthorizedClass() {
		return Predicate.class;
	}
}
