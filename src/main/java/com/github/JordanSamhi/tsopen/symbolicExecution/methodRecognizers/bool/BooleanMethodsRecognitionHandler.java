package com.github.JordanSamhi.tsopen.symbolicExecution.methodRecognizers.bool;

import java.util.List;

import com.github.JordanSamhi.tsopen.symbolicExecution.SymbolicExecution;
import com.github.JordanSamhi.tsopen.symbolicExecution.symbolicValues.SymbolicValue;

import soot.SootMethod;
import soot.Value;

public abstract class BooleanMethodsRecognitionHandler implements BooleanMethodsRecognition {

	private BooleanMethodsRecognitionHandler next;
	protected SymbolicExecution se;

	public BooleanMethodsRecognitionHandler(BooleanMethodsRecognitionHandler next, SymbolicExecution se) {
		this.next = next;
		this.se = se;
	}

	@Override
	public boolean recognizeBooleanMethod(SootMethod method, Value base, SymbolicValue sv, List<Value> args) {
		boolean recognized = this.processBooleanMethod(method, base, sv, args);

		if(recognized) {
			return recognized;
		}
		if(this.next != null) {
			return this.next.recognizeBooleanMethod(method, base, sv, args);
		}
		else {
			return false;
		}
	}
}
