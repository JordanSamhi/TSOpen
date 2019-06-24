package com.github.dusby.tsopen.symbolicExecution.methodRecognizers.numerics;

import java.util.List;

import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;

import soot.SootMethod;
import soot.Value;

public interface LongMethodsRecognition {
	public List<SymbolicValue> recognizeLongMethod(SootMethod method, Value base, List<Value> args, SymbolicValue sv);
	public List<SymbolicValue> processLongMethod(SootMethod method, Value base, List<Value> args, SymbolicValue sv);
	public boolean containsTag(Value base, String nowTag);
}