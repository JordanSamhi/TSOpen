package com.github.dusby.symbolicExecution.typeRecognizers;

import java.util.Map;

import com.github.dusby.symbolicExecution.symbolicValues.SymbolicValueProvider;
import soot.Value;
import soot.jimple.DefinitionStmt;

public interface RecognizerProvider {
	public Map<Value, SymbolicValueProvider> recognize(DefinitionStmt def);
	public Map<Value, SymbolicValueProvider> processRecognition(DefinitionStmt def);

}