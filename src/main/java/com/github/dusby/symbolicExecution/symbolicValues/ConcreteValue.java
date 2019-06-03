package com.github.dusby.symbolicExecution.symbolicValues;

import soot.jimple.Constant;

public class ConcreteValue implements SymbolicValueProvider {
	
	private Constant constant;
	
	public ConcreteValue(Constant c) {
		this.constant = c;
	}

	@Override
	public String getContextValue() {
		return this.constant.toString();
	}
}
