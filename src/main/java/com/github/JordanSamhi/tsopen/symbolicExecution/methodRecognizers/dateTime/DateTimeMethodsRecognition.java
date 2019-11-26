package com.github.JordanSamhi.tsopen.symbolicExecution.methodRecognizers.dateTime;

import com.github.JordanSamhi.tsopen.symbolicExecution.symbolicValues.SymbolicValue;

import soot.SootMethod;

public interface DateTimeMethodsRecognition {
	public boolean recognizeDateTimeMethod(SootMethod method, SymbolicValue sv);
	public boolean processDateTimeMethod(SootMethod method, SymbolicValue sv);
}
