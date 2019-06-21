package com.github.dusby.tsopen.symbolicExecution.methodRecognizers.strings;

import java.util.ArrayList;
import java.util.List;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;

import soot.SootMethod;
import soot.Value;

public class ToStringRecognition extends StringMethodsRecognitionHandler {

	public ToStringRecognition(StringMethodsRecognitionHandler next, SymbolicExecution se) {
		super(next, se);
	}

	@Override
	public List<SymbolicValue> processRecognition(SootMethod method, Value base, List<Value> args) {
		List<SymbolicValue> results = new ArrayList<SymbolicValue>();
		if(method.getName().equals(TOSTRING)) {
			this.addSimpleResult(base, results);
			return results;
		}
		return null;
	}

}
