package com.github.dusby.tsopen.symbolicExecution.methodRecognizers.strings;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecutioner;

import soot.SootMethod;
import soot.Value;

public abstract class StringMethodsRecognizerProcessor implements StringMethodsRecognizerProvider {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	protected static final String APPEND = "append";
	protected static final String VALUEOF = "valueOf";
	protected static final String SUBSTRING = "substring";
	protected static final String TOSTRING = "toString";
	protected static final String UNKNOWN_STRING = "UNKNOWN_STRING";
	protected static final String NULL = "null";

	private StringMethodsRecognizerProcessor next;
	protected SymbolicExecutioner se;

	public StringMethodsRecognizerProcessor(StringMethodsRecognizerProcessor next, SymbolicExecutioner se) {
		this.next = next;
		this.se = se;
	}

	@Override
	public List<String> recognize(SootMethod method, Value base, List<Value> args) {
		List<String> result = this.processRecognition(method, base, args);

		if(result != null) {
			return result;
		}
		if(this.next != null) {
			return this.next.recognize(method, base, args);
		}
		else {
			return null;
		}
	}
}