package com.github.dusby.tsopen.symbolicExecution.symbolicValues;

public interface SymbolicValueProvider {
	public String getValue();
	public boolean isSymbolic();
	public boolean isConcrete();
	public boolean isMethodRepresentation();
}