package com.github.dusby.tsopen.symbolicExecution.symbolicValues;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;

import soot.Value;

public class FieldValue extends AbstractSymbolicValue {

	private Value base;
	private String field;

	public FieldValue(Value base, String field, SymbolicExecution se) {
		super(se);
		this.base = base;
		this.field = field;
	}

	@Override
	public String getValue() {
		return String.format("%s.%s", this.base == null ? "" : this.base, this.field);
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
