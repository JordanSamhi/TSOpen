package com.github.dusby.tsopen.symbolicExecution.methodRecognizers.strings;

import java.util.List;

import soot.SootMethod;
import soot.Value;

public interface StringMethodsRecognizerProvider {
	public String recognize(SootMethod method, Value base, List<Value> args);
	public String processRecognition(SootMethod method, Value base, List<Value> args);
}
