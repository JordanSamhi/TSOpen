package com.github.dusby.tsopen.symbolicExecution.methodRecognizers.numeric;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;

import soot.SootMethod;
import soot.Value;

public abstract class NumericMethodsRecognitionHandler implements NumericMethodsRecognition {

	private NumericMethodsRecognitionHandler next;
	protected SymbolicExecution se;

	public NumericMethodsRecognitionHandler(NumericMethodsRecognitionHandler next, SymbolicExecution se) {
		this.next = next;
		this.se = se;
	}

	@Override
	public boolean recognizeNumericMethod(SootMethod method, Value base, SymbolicValue sv) {
		boolean recognized = this.processNumericMethod(method, base, sv);

		if(recognized) {
			return recognized;
		}
		if(this.next != null) {
			return this.next.recognizeNumericMethod(method, base, sv);
		}
		else {
			return false;
		}
	}
}
