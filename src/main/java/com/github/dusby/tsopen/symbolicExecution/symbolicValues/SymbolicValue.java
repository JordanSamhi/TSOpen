package com.github.dusby.tsopen.symbolicExecution.symbolicValues;

public interface SymbolicValue {
	public String getValue();
	public boolean isSymbolic();
	public boolean isConstant();
	public boolean isMethodRepresentation();
	public boolean isObject();
}