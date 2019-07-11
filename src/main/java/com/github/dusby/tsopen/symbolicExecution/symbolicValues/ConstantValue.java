package com.github.dusby.tsopen.symbolicExecution.symbolicValues;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;

import soot.jimple.Constant;

public class ConstantValue extends ConcreteValue {

	private Constant constant;

	public ConstantValue(Constant c, SymbolicExecution se) {
		super(se);
		this.constant = c;
	}

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

	public Constant getConstant() {
		return this.constant;
	}
}
