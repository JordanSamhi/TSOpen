package com.github.dusby.tsopen.symbolicExecution.methodRecognizers.strings;

import java.util.ArrayList;
import java.util.List;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecutioner;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValueProvider;

import soot.SootMethod;
import soot.Value;

public class ToStringRecognizer extends StringMethodsRecognizerProcessor {

	public ToStringRecognizer(StringMethodsRecognizerProcessor next, SymbolicExecutioner se) {
		super(next, se);
	}

	@Override
	public List<SymbolicValueProvider> processRecognition(SootMethod method, Value base, List<Value> args) {
		List<SymbolicValueProvider> results = new ArrayList<SymbolicValueProvider>();
		if(method.getName().equals(TOSTRING)) {
			this.addSimpleResult(base, results);
			return results;
		}
		return null;
	}

}
