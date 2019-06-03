package com.github.dusby.predicates;

import java.util.LinkedList;

public abstract class PredicateAggregate implements PredicateProvider {
	
	protected LinkedList<PredicateProvider> predicates;
	
	public PredicateAggregate() {
		this.predicates = new LinkedList<PredicateProvider>();
	}
	
	public PredicateAggregate(PredicateProvider predicate) {
		this.predicates = new LinkedList<PredicateProvider>();
		this.predicates.add(predicate);
	}
	
	public void addPredicate(PredicateProvider p) {
		if(p != null) {
			this.predicates.add(p);
		}
	}
	
	public void deleteLastPredicate() {
		if(!this.isEmpty()) {
			this.predicates.removeLast();
		}
	}
	
	public PredicateProvider getLastPredicate() {
		if(this.isEmpty()) {
			return null;
		}
		return this.predicates.getLast();
	}
	
	public boolean isEmpty() {
		return this.predicates.isEmpty();
	}
	
	public void empty() {
		this.predicates.clear();
	}
	
	@Override
	public String toString() {
		String pString = "";
		for(PredicateProvider p : this.predicates) {
			pString += "(" + p + ")";
			if(p != this.predicates.getLast()) {
				pString += this.getSymbol();
			}
		}
		return pString;
	}
	
	public int getSize() {
		return this.predicates.size();
	}
	
	public void printPredicate() {
		System.out.println(this);
	}
}
