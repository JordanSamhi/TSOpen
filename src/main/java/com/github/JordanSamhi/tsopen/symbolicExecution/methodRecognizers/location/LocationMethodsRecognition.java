package com.github.JordanSamhi.tsopen.symbolicExecution.methodRecognizers.location;

import com.github.JordanSamhi.tsopen.symbolicExecution.symbolicValues.SymbolicValue;

import soot.SootMethod;

public interface LocationMethodsRecognition {
	public boolean recognizeLocationMethod(SootMethod method, SymbolicValue sv);
	public boolean processLocationMethod(SootMethod method, SymbolicValue sv);
}
