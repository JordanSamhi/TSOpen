package com.github.dusby.tsopen.symbolicExecution.typeRecognizers;

import org.javatuples.Pair;

import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValueProvider;

import soot.Value;
import soot.jimple.DefinitionStmt;

public interface RecognizerProvider {
	public Pair<Value, SymbolicValueProvider> recognize(DefinitionStmt def);
	public Pair<Value, SymbolicValueProvider> processRecognition(DefinitionStmt def);

}