package com.github.JordanSamhi.tsopen.symbolicExecution.methodRecognizers.location;

import com.github.JordanSamhi.tsopen.symbolicExecution.SymbolicExecution;
import com.github.JordanSamhi.tsopen.symbolicExecution.symbolicValues.SymbolicValue;

import soot.SootMethod;

public abstract class LocationMethodsRecognitionHandler implements LocationMethodsRecognition {

	private LocationMethodsRecognitionHandler next;
	protected SymbolicExecution se;

	public LocationMethodsRecognitionHandler(LocationMethodsRecognitionHandler next, SymbolicExecution se) {
		this.next = next;
		this.se = se;
	}

	@Override
	public boolean recognizeLocationMethod(SootMethod method, SymbolicValue sv) {
		boolean recognized = this.processLocationMethod(method, sv);

		if(recognized) {
			return recognized;
		}
		if(this.next != null) {
			return this.next.recognizeLocationMethod(method, sv);
		}
		else {
			return false;
		}
	}
}
