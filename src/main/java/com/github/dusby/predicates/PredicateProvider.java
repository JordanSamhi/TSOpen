package com.github.dusby.predicates;

public interface PredicateProvider {
	public String getSymbol();
	public boolean isEquivalentTo(PredicateProvider p);
}
