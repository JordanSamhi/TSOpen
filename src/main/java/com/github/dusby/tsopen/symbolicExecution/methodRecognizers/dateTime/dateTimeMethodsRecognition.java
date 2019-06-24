package com.github.dusby.tsopen.symbolicExecution.methodRecognizers.dateTime;

import java.util.List;

import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;

import soot.SootMethod;
import soot.Value;

public interface dateTimeMethodsRecognition {
	public boolean recognizeLongMethod(SootMethod method, List<Value> args, SymbolicValue sv);
	public boolean processLongMethod(SootMethod method, List<Value> args, SymbolicValue sv);
}
