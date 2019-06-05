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
	
	public void deleteLastPredicate() {
		if(!this.isEmpty()) {
			this.predicates.removeLast();
		}
	}
	
	public PredicateProvider getLastPredicate() {
		if(this.predicates.isEmpty()) {
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
	
	public boolean contains(PredicateProvider predicate) {
		return this.predicates.contains(predicate);
	}
	
	@Override
	public String toString() {
		if(!this.isEmpty()) {
			String pString = "";
			for(PredicateProvider p : this.predicates) {
				pString += String.format("({})", p);
				if(p != this.predicates.getLast()) {
					pString += this.getSymbol();
				}
			}
			return pString;
		}
		else {
			return "No predicate";
		}
	}
	
	public int getSize() {
		return this.predicates.size();
	}
	
	protected void checkPredicateClass(PredicateProvider p) {
		if(p == null) {
			throw new NullPointerException();
		}
		if(p.getClass() != this.getAuthorizedClass()) {
			throw new IllegalArgumentException(String.format("{} => {} provided.", this.getAuthorizedClassMessage(), p.getClass().getName()));
		}
	}
	
	public boolean isRedundant(PredicateProvider p) {
		for(PredicateProvider provider : this.predicates) {
			if(p.isEquivalentTo(provider)) {
				return true;
			}
		}
		return false;
	}
	
	public void addPredicate(PredicateProvider p) {
		this.checkPredicateClass(p);
		this.predicates.add(p);
	}
	
	public boolean isEquivalentTo(PredicateProvider p) {
		if (this == p) {
			return true;
		}
		if(p == null || this.getClass() != p.getClass()) {
			return false;
		}
		PredicateAggregate predicate = (PredicateAggregate) p;
		if(predicate.getSize() != this.getSize()) {
			return false;
		}
		boolean equiv = false;
		for(PredicateProvider pThis : this.predicates) {
			for(PredicateProvider pCP : predicate.predicates) {
				if(pThis.isEquivalentTo(pCP)) {
					equiv = true;
				}
			}
			if(!equiv) {
				return false;
			}
			equiv = false;
		}
		return true;
	}
	
	protected abstract Class<? extends PredicateProvider> getAuthorizedClass();
	protected abstract String getAuthorizedClassMessage();
}
