package com.github.JordanSamhi.tsopen.symbolicExecution.symbolicValues;

import com.github.JordanSamhi.tsopen.symbolicExecution.SymbolicExecution;
import com.github.JordanSamhi.tsopen.utils.Constants;

public class UnknownValue extends AbstractSymbolicValue {

	private String additionalValues;

	public UnknownValue(SymbolicExecution se) {
		super(se);
		this.additionalValues = "";
	}

	@Override
	public String getValue() {
		if(this.additionalValues.isEmpty()) {
			return Constants.UNKNOWN_VALUE;
		}else {
			return String.format("%s_%s", Constants.UNKNOWN_VALUE, this.additionalValues);
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
