package com.github.dusby.tsopen.symbolicExecution.methodRecognizers.strings;

import java.util.List;

import soot.SootMethod;
import soot.Value;

public interface StringMethodsRecognizerProvider {
	public List<String> recognize(SootMethod method, Value base, List<Value> args);
	public List<String> processRecognition(SootMethod method, Value base, List<Value> args);
}
