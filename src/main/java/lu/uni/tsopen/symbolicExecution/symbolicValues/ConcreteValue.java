package lu.uni.tsopen.symbolicExecution.symbolicValues;

import lu.uni.tsopen.symbolicExecution.SymbolicExecution;

public abstract class ConcreteValue extends AbstractSymbolicValue {

	public ConcreteValue(SymbolicExecution se) {
		super(se);
	}

	@Override
	public boolean isSymbolic() {
		return false;
	}

	@Override
	public boolean isMethodRepresentation() {
		return false;
	}
}
