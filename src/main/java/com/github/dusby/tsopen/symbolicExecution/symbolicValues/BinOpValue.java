package com.github.dusby.tsopen.symbolicExecution.symbolicValues;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;

import soot.Value;

public class BinOpValue extends AbstractSymbolicValue {

	private Value op1;
	private Value op2;
	private String symbol;

	public BinOpValue(SymbolicExecution se, Value op1, Value op2, String symbol) {
		super(se);
		this.op1 = op1;
		this.op2 = op2;
		this.symbol = symbol;
		this.getSymbolicValues(this.op1);
		this.getSymbolicValues(this.op2);
	}

	@Override
	public String getValue() {
		return String.format("%s%s%s", this.op1, this.symbol, this.op2);
	}

	@Override
	public boolean isSymbolic() {
		return true;
	}

	@Override
	public boolean isConstant() {
		return false;
	}

	@Override
	public boolean isMethodRepresentation() {
		return false;
	}

	@Override
	public boolean isObject() {
		return false;
	}
}
