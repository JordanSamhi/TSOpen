package predicates;

import java.util.ArrayList;

public abstract class PredicateAggregate implements PredicateProvider{
	
	protected ArrayList<PredicateProvider> predicates;
	
	public PredicateAggregate() {
		this.predicates = new ArrayList<PredicateProvider>();
	}
	
	public PredicateAggregate(PredicateProvider predicate) {
		this.predicates = new ArrayList<PredicateProvider>();
		this.predicates.add(predicate);
	}
	
	public void addPredicate(PredicateProvider p) {
		if(p != null) {
			this.predicates.add(p);
		}
	}
	
	public void deleteLastPredicate() {
		if(!this.isEmpty()) {
			this.predicates.remove(this.predicates.size() - 1);
		}
	}
	
	public PredicateProvider getLastPredicate() {
		if(this.isEmpty()) {
			return null;
		}
		return this.predicates.get(this.predicates.size() - 1);
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
			if(p != this.predicates.get(this.predicates.size() - 1)) {
				pString += this.getSymbol();
			}
		}
		return pString;
	}
	
	public void printPredicate() {
		System.out.println(this);
	}
}
