package com.github.dusby.tsopen.symbolicExecution.methodRecognizers.strings;

import java.util.List;

import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValueProvider;

import soot.SootMethod;
import soot.Value;

public interface StringMethodsRecognizerProvider {
	public List<SymbolicValueProvider> recognize(SootMethod method, Value base, List<Value> args);
	public List<SymbolicValueProvider> processRecognition(SootMethod method, Value base, List<Value> args);
}
