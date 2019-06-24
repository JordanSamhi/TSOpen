package com.github.dusby.tsopen.symbolicExecution.methodRecognizers.numeric;

import java.util.List;

import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;

import soot.SootMethod;
import soot.Value;

public interface NumericMethodsRecognition {
	public boolean recognizeNumericMethod(SootMethod method, Value base, List<Value> args, SymbolicValue sv);
	public boolean processNumericMethod(SootMethod method, Value base, List<Value> args, SymbolicValue sv);
	public boolean containsTag(Value base, String nowTag);
}