package predicates;

import java.util.ArrayList;
import java.util.List;

public class ConjunctionPredicate extends PredicateAggregate{

	public static final String SYMBOL = " /\\ ";
	private ArrayList<PredicateProvider> predicates;
	
	public ConjunctionPredicate() {
		this.predicates = new ArrayList<PredicateProvider>();
	}
	
	public ConjunctionPredicate(PredicateProvider predicate) {
		this.predicates = new ArrayList<PredicateProvider>();
		this.predicates.add(predicate);
	}
	
	public void addPredicate(PredicateProvider p) {
		if(p != null) {
			this.predicates.add(p);
		}
	}
	
	@Override
	public String getSymbol() {
		return SYMBOL;
	}

	@Override
	public List<PredicateProvider> getPredicates() {
		return this.predicates;
	}
}
