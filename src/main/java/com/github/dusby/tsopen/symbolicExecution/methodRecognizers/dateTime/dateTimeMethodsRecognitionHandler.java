package com.github.dusby.tsopen.symbolicExecution.methodRecognizers.dateTime;

import java.util.List;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;

import soot.SootMethod;
import soot.Value;

public abstract class dateTimeMethodsRecognitionHandler implements dateTimeMethodsRecognition {

	private dateTimeMethodsRecognitionHandler next;
	protected SymbolicExecution se;

	public dateTimeMethodsRecognitionHandler(dateTimeMethodsRecognitionHandler next, SymbolicExecution se) {
		this.next = next;
		this.se = se;
	}

	@Override
	public boolean recognizeDateTimeMethod(SootMethod method, List<Value> args, SymbolicValue sv) {
		boolean recognized = this.processDateTimeMethod(method, args, sv);

		if(recognized) {
			return recognized;
		}
		if(this.next != null) {
			return this.next.recognizeDateTimeMethod(method, args, sv);
		}
		else {
			return false;
		}
	}
}
