package com.github.dusby.tsopen.symbolicExecution.typeRecognizers;

import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValueProvider;

import soot.Unit;
import soot.jimple.DefinitionStmt;

public interface RecognizerProvider {
	public SymbolicValueProvider recognize(DefinitionStmt defUnit, Unit node);
	public SymbolicValueProvider processRecognition(DefinitionStmt defUnit, Unit node);

}