package com.github.dusby.tsopen.symbolicExecution.symbolicValues;

import soot.jimple.Constant;

public class ConstantValue extends ConcreteValue {

	private Constant constant;

	public ConstantValue(Constant c) {
		super();
		this.constant = c;
	}

	//FIXME find better solution
	@Override
	public String getValue() {
		return this.constant.toString().replace("\"", "").replace("\\", "");
	}

	@Override
	public boolean isConstant() {
		return true;
	}

	@Override
	public boolean isObject() {
		return false;
	}
}
