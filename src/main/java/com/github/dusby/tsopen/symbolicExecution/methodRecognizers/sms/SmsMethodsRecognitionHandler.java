package com.github.dusby.tsopen.symbolicExecution.methodRecognizers.sms;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;

import soot.SootMethod;

public abstract class SmsMethodsRecognitionHandler implements SmsMethodsRecognition {

	private SmsMethodsRecognitionHandler next;
	protected SymbolicExecution se;

	public SmsMethodsRecognitionHandler(SmsMethodsRecognitionHandler next, SymbolicExecution se) {
		this.next  = next;
		this.se = se;
	}

	@Override
	public boolean recognizeSmsMethod(SootMethod method, SymbolicValue sv) {
		boolean recognized = this.processSmsMethod(method, sv);

		if(recognized) {
			return recognized;
		}
		if(this.next != null) {
			return this.next.recognizeSmsMethod(method, sv);
		}
		else {
			return false;
		}
	}
}
