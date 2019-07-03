package com.github.dusby.tsopen.symbolicExecution.methodRecognizers.strings;

import java.util.ArrayList;
import java.util.List;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import com.github.dusby.tsopen.utils.Constants;
import com.github.dusby.tsopen.utils.Utils;

import soot.SootMethod;
import soot.Value;

public class ToUpperCaseRecognition extends StringMethodsRecognitionHandler {

	public ToUpperCaseRecognition(StringMethodsRecognitionHandler next, SymbolicExecution se) {
		super(next, se);
	}

	@Override
	public List<SymbolicValue> processStringMethod(SootMethod method, Value base, List<Value> args) {
		List<SymbolicValue> results = new ArrayList<SymbolicValue>();
		if(method.getName().equals(Constants.TO_UPPER_CASE)) {
			this.addSimpleResult(base, results);
			for(SymbolicValue sv : results) {
				Utils.propagateTags(base, sv, this.se);
			}
			return results;
		}
		return null;
	}

}
