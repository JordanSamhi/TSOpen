package com.github.dusby.tsopen.symbolicExecution.methodRecognizers.location;

import java.util.List;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;

import soot.SootMethod;
import soot.Value;

public abstract class LocationMethodsRecognitionHandler implements LocationMethodsRecognition {

	private LocationMethodsRecognitionHandler next;
	protected SymbolicExecution se;

	public LocationMethodsRecognitionHandler(LocationMethodsRecognitionHandler next, SymbolicExecution se) {
		this.next = next;
		this.se = se;
	}

	@Override
	public boolean recognizeLocationMethod(SootMethod method, List<Value> args, SymbolicValue sv) {
		boolean recognized = this.processLocationMethod(method, args, sv);

		if(recognized) {
			return recognized;
		}
		if(this.next != null) {
			return this.next.recognizeLocationMethod(method, args, sv);
		}
		else {
			return false;
		}
	}
}
