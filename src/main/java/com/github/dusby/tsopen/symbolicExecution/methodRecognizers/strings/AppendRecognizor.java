package com.github.dusby.tsopen.symbolicExecution.methodRecognizers.strings;

import java.util.List;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecutioner;

import soot.SootMethod;
import soot.Value;

public class AppendRecognizor extends StringMethodsRecognizerProcessor {

	public AppendRecognizor(StringMethodsRecognizerProcessor next, SymbolicExecutioner se) {
		super(next, se);
	}

	@Override
	public String processRecognition(SootMethod method, Value base, List<Value> args) {
		if(method.getName().equals(APPEND)) {
		}
		return null;
	}

}
