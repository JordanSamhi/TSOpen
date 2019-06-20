package com.github.dusby.tsopen.symbolicExecution.symbolicValues;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecutioner;

public abstract class ConcreteValue extends AbstractSymbolicValue {

	public ConcreteValue() {
		super();
	}

	public ConcreteValue(SymbolicExecutioner se) {
		super(se);
	}

	@Override
	public String toString() {
		return this.getValue();
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
