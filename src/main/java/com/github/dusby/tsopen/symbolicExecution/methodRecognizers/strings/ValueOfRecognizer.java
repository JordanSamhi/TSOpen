package com.github.dusby.tsopen.symbolicExecution.methodRecognizers.strings;

import java.util.List;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecutioner;

import soot.SootMethod;
import soot.Value;

public class ValueOfRecognizer extends StringMethodsRecognizerProcessor {

	public ValueOfRecognizer(StringMethodsRecognizerProcessor next, SymbolicExecutioner se) {
		super(next, se);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<String> processRecognition(SootMethod method, Value base, List<Value> args) {
		if(method.getName().equals(VALUEOF)) {
			Value effectiveArg = args.get(0);
		}
		return null;
	}

}
