package com.github.dusby.tsopen.symbolicExecution.methodRecognizers.location;

import java.util.List;

import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;

import soot.SootMethod;
import soot.Value;

public interface LocationMethodsRecognition {
	public boolean recognizeLocationMethod(SootMethod method, List<Value> args, SymbolicValue sv);
	public boolean processLocationMethod(SootMethod method, List<Value> args, SymbolicValue sv);
}
