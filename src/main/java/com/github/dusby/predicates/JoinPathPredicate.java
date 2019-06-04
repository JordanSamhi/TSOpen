package com.github.dusby.predicates;

public class JoinPathPredicate extends PredicateAggregate {

	public static final String SYMBOL = " \\/ ";
	
	public JoinPathPredicate() {
		super();
	}
	
	public JoinPathPredicate(PredicateProvider predicate) {
		super(predicate);
	}
	
	@Override
	public String getSymbol() {
		return SYMBOL;
	}

	@Override
	protected Class<? extends PredicateProvider> getAuthorizedClass() {
		return PathPredicate.class;
	}

	@Override
	protected String getAuthorizedClassMessage() {
		return "JoinPathPredicate class only accepts predicates of PathPredicate class ((P0 /\\ ... /\\ Pn) \\/ ... \\/ (P0 /\\ ... /\\ Pn))";
	}
}