package com.github.dusby.tsopen.symbolicExecution.typeRecognizers;

import org.javatuples.Pair;

import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValueProvider;

import soot.Unit;
import soot.Value;

public interface RecognizerProvider {
	public Pair<Value, SymbolicValueProvider> recognize(Unit node);
	public Pair<Value, SymbolicValueProvider> processRecognition(Unit node);

}