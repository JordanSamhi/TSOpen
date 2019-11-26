package com.github.JordanSamhi.tsopen.symbolicExecution.methodRecognizers.strings;

import java.util.ArrayList;
import java.util.List;

import com.github.JordanSamhi.tsopen.symbolicExecution.SymbolicExecution;
import com.github.JordanSamhi.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import com.github.JordanSamhi.tsopen.utils.Constants;
import com.github.JordanSamhi.tsopen.utils.Utils;

import soot.SootMethod;
import soot.Value;

public class ToStringRecognition extends StringMethodsRecognitionHandler {

	public ToStringRecognition(StringMethodsRecognitionHandler next, SymbolicExecution se) {
		super(next, se);
	}

	@Override
	public List<SymbolicValue> processStringMethod(SootMethod method, Value base, List<Value> args) {
		List<SymbolicValue> results = new ArrayList<SymbolicValue>();
		if(method.getName().equals(Constants.TOSTRING)) {
			this.addSimpleResult(base, results);
			for(SymbolicValue sv : results) {
				Utils.propagateTags(base, sv, this.se);
			}
			return results;
		}
		return null;
	}

}
