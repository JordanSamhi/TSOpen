package com.github.dusby.tsopen.symbolicExecution.methodRecognizers.dateTime;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;

import soot.SootMethod;

public abstract class DateTimeMethodsRecognitionHandler implements DateTimeMethodsRecognition {

	private DateTimeMethodsRecognitionHandler next;
	protected SymbolicExecution se;

	public DateTimeMethodsRecognitionHandler(DateTimeMethodsRecognitionHandler next, SymbolicExecution se) {
		this.next = next;
		this.se = se;
	}

	@Override
	public boolean recognizeDateTimeMethod(SootMethod method, SymbolicValue sv) {
		boolean recognized = this.processDateTimeMethod(method, sv);

		if(recognized) {
			return recognized;
		}
		if(this.next != null) {
			return this.next.recognizeDateTimeMethod(method, sv);
		}
		else {
			return false;
		}
	}
}
