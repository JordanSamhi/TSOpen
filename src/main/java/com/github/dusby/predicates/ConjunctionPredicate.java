package com.github.dusby.predicates;

public class ConjunctionPredicate extends PredicateAggregate {

	public static final String SYMBOL = " /\\ ";
	
	public ConjunctionPredicate() {
		super();
	}
	
	public ConjunctionPredicate(PredicateProvider predicate) {
		super(predicate);
	}
	
	public ConjunctionPredicate(ConjunctionPredicate cp) {
		super();
		for(PredicateProvider p : cp.predicates) {
			Predicate predicate = (Predicate) p;
			this.predicates.add(new Predicate(predicate));
		}
	}
	
	@Override
	public String getSymbol() {
		return SYMBOL;
	}
}
