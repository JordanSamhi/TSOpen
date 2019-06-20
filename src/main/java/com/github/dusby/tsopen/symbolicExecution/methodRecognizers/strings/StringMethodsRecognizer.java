package com.github.dusby.tsopen.symbolicExecution.methodRecognizers.strings;

import java.util.List;

import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;

import soot.SootMethod;
import soot.Value;

public interface StringMethodsRecognizer {
	public List<SymbolicValue> recognize(SootMethod method, Value base, List<Value> args);
	public List<SymbolicValue> processRecognition(SootMethod method, Value base, List<Value> args);
}
