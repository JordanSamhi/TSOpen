package com.github.JordanSamhi.tsopen.symbolicExecution.methodRecognizers.numeric;

import com.github.JordanSamhi.tsopen.symbolicExecution.symbolicValues.SymbolicValue;

import soot.SootMethod;
import soot.Value;

public interface NumericMethodsRecognition {
	public boolean recognizeNumericMethod(SootMethod method, Value base, SymbolicValue sv);
	public boolean processNumericMethod(SootMethod method, Value base, SymbolicValue sv);
	public boolean genericProcessNumericMethod(SootMethod method, Value base, SymbolicValue sv,
			String className, String methodName, String containedTag, String addedTag);
	public boolean isTagHandled(String containedTag, String addedTag, Value base, SymbolicValue sv);
}