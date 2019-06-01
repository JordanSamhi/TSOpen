package predicates;

import java.util.ArrayList;
import java.util.List;

public class DisjunctionPredicate extends PredicateAggregate{

	public static final String SYMBOL = " \\/ ";
	private ArrayList<PredicateProvider> conjunctionPredicates;
	
	public DisjunctionPredicate() {
		this.conjunctionPredicates = new ArrayList<PredicateProvider>();
	}
	
	public DisjunctionPredicate(PredicateProvider conjunctionPredicate) {
		this.conjunctionPredicates = new ArrayList<PredicateProvider>();
		this.conjunctionPredicates.add(conjunctionPredicate);
	}
	
	public void addPredicate(PredicateProvider cp) {
		if(cp != null) {
			this.conjunctionPredicates.add(cp);
		}
	}

	@Override
	public String getSymbol() {
		return SYMBOL;
	}

	@Override
	public List<PredicateProvider> getPredicates() {
		return this.conjunctionPredicates;
	}
}
