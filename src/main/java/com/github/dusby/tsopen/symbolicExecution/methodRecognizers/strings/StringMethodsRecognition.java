package com.github.dusby.tsopen.symbolicExecution.methodRecognizers.strings;

import java.util.List;

import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;

import soot.SootMethod;
import soot.Value;

public interface StringMethodsRecognition {
	//TODO Change those name + add long method recognition
	public List<SymbolicValue> recognizeStringMethod(SootMethod method, Value base, List<Value> args);
	public List<SymbolicValue> processStringMethod(SootMethod method, Value base, List<Value> args);
}
