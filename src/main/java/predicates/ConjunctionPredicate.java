package predicates;

public class ConjunctionPredicate extends PredicateAggregate{

	public static final String SYMBOL = " /\\ ";
	
	public ConjunctionPredicate() {
		super();
	}
	
	public ConjunctionPredicate(PredicateProvider predicate) {
		super(predicate);
	}
	
	@Override
	public String getSymbol() {
		return SYMBOL;
	}
}
