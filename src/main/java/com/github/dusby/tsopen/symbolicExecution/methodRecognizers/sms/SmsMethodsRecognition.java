package com.github.dusby.tsopen.symbolicExecution.methodRecognizers.sms;

import java.util.List;

import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;

import soot.SootMethod;
import soot.Value;

public interface SmsMethodsRecognition {
	public boolean recognizeSmsMethod(SootMethod method, List<Value> args, SymbolicValue sv);
	public boolean processSmsMethod(SootMethod method, List<Value> args, SymbolicValue sv);
}
