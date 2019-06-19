package com.github.dusby.tsopen.symbolicExecution.symbolicValues;

import soot.jimple.Constant;

public class ConcreteValue implements SymbolicValueProvider {

	private Constant constant;

	public ConcreteValue(Constant c) {
		this.constant = c;
	}

	//FIXME find better solution
	@Override
	public String getValue() {
		return this.constant.toString().replace("\"", "").replace("\\", "");
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
	public boolean isConcrete() {
		return true;
	}

	@Override
	public boolean isMethodRepresentation() {
		return false;
	}
}
