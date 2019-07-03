package com.github.dusby.tsopen.symbolicExecution.methodRecognizers.strings;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dusby.tsopen.symbolicExecution.ContextualValues;
import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.UnknownValue;

import soot.SootMethod;
import soot.Value;

public abstract class StringMethodsRecognitionHandler implements StringMethodsRecognition {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	private StringMethodsRecognitionHandler next;
	protected SymbolicExecution se;

	public StringMethodsRecognitionHandler(StringMethodsRecognitionHandler next, SymbolicExecution se) {
		this.next = next;
		this.se = se;
	}

	@Override
	public List<SymbolicValue> recognizeStringMethod(SootMethod method, Value base, List<Value> args) {
		List<SymbolicValue> result = this.processStringMethod(method, base, args);

		if(result != null && !result.isEmpty()) {
			return result;
		}
		if(this.next != null) {
			return this.next.recognizeStringMethod(method, base, args);
		}
		else {
			return null;
		}
	}

	protected void addSimpleResult(Value v, List<SymbolicValue> results) {
		ContextualValues contextualValues = this.se.getContext().get(v);
		List<SymbolicValue> values = null;
		if(contextualValues == null) {
			results.add(new UnknownValue(this.se));
		}else {
			values = contextualValues.getLastCoherentValues(null);
			if(values != null) {
				for(SymbolicValue sv : values) {
					results.add(sv);
				}
			}
		}
	}
}