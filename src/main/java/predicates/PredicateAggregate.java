package predicates;

import java.util.List;

public abstract class PredicateAggregate implements PredicateProvider{
	
	public void deleteLastPredicate() {
		if(!this.isEmpty()) {
			this.getPredicates().remove(this.getPredicates().size() - 1);
		}
	}
	
	public PredicateProvider getLastPredicate() {
		if(this.isEmpty()) {
			return null;
		}
		return this.getPredicates().get(this.getPredicates().size() - 1);
	}
	
	public boolean isEmpty() {
		return this.getPredicates().isEmpty();
	}
	
	@Override
	public String toString() {
		String pString = "";
		for(PredicateProvider p : this.getPredicates()) {
			pString += "(" + p + ")";
			if(p != this.getPredicates().get(this.getPredicates().size() - 1)) {
				pString += this.getSymbol();
			}
		}
		return pString;
	}
	
	public void printPredicate() {
		System.out.println(this);
	}
	
	public abstract List<PredicateProvider> getPredicates();
}
