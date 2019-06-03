package predicates;

public class DisjunctionPredicate extends PredicateAggregate{

	public static final String SYMBOL = " \\/ ";
	
	public DisjunctionPredicate() {
		super();
	}
	
	public DisjunctionPredicate(PredicateProvider conjunctionPredicate) {
		super(conjunctionPredicate);
	}
	
	@Override
	public String getSymbol() {
		return SYMBOL;
	}
}
