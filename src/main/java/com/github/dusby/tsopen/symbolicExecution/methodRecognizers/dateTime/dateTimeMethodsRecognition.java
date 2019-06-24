package com.github.dusby.tsopen.symbolicExecution.methodRecognizers.dateTime;

import java.util.List;

import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;

import soot.SootMethod;
import soot.Value;

public interface dateTimeMethodsRecognition {
	public boolean recognizeDateTimeMethod(SootMethod method, List<Value> args, SymbolicValue sv);
	public boolean processDateTimeMethod(SootMethod method, List<Value> args, SymbolicValue sv);
}
