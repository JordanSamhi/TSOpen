package com.github.dusby.tsopen.symbolicExecution.methodRecognizers.sms;

import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;

import soot.SootMethod;

public interface SmsMethodsRecognition {
	public boolean recognizeSmsMethod(SootMethod method, SymbolicValue sv);
	public boolean processSmsMethod(SootMethod method, SymbolicValue sv);
}
