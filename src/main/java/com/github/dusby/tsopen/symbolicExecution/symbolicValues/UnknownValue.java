package com.github.dusby.tsopen.symbolicExecution.symbolicValues;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;

public class UnknownValue extends AbstractSymbolicValue {

	private static final String UNKNOWN_VALUE = "{#}";
	private String additionalValues;

	public UnknownValue(SymbolicExecution se) {
		super(se);
		this.additionalValues = "";
	}

	@Override
	public String toString() {
		return this.getValue();
	}

	@Override
	public String getValue() {
		if(this.additionalValues.isEmpty()) {
			return UNKNOWN_VALUE;
		}else {
			return String.format("%s_%s", UNKNOWN_VALUE, this.additionalValues);
		}
	}

	public void addValue(String s) {
		this.additionalValues += s;
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
