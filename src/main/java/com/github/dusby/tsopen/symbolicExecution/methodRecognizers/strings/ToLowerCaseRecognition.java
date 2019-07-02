package com.github.dusby.tsopen.symbolicExecution.methodRecognizers.strings;

import java.util.ArrayList;
import java.util.List;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import com.github.dusby.tsopen.utils.Constants;

import soot.SootMethod;
import soot.Value;

public class ToLowerCaseRecognition extends StringMethodsRecognitionHandler {

	public ToLowerCaseRecognition(StringMethodsRecognitionHandler next, SymbolicExecution se) {
		super(next, se);
	}

	@Override
	public List<SymbolicValue> processStringMethod(SootMethod method, Value base, List<Value> args) {
		List<SymbolicValue> results = new ArrayList<SymbolicValue>();
		if(method.getName().equals(Constants.TO_LOWER_CASE)) {
			this.addSimpleResult(base, results);
			return results;
		}
		return null;
	}

}
