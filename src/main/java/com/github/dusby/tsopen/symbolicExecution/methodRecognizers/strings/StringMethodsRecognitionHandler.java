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
	protected static final String APPEND = "append";
	protected static final String VALUEOF = "valueOf";
	protected static final String SUBSTRING = "substring";
	protected static final String TOSTRING = "toString";
	protected static final String UNKNOWN_STRING = "UNKNOWN_STRING";
	protected static final String NULL = "null";
	protected static final String GET_MESSAGE_BODY = "getMessageBody";
	protected static final String GET_DISPLAY_MESAGE_BODY = "getDisplayMessageBody";

	private StringMethodsRecognitionHandler next;
	protected SymbolicExecution se;

	public StringMethodsRecognitionHandler(StringMethodsRecognitionHandler next, SymbolicExecution se) {
		this.next = next;
		this.se = se;
	}

	@Override
	public List<SymbolicValue> recognize(SootMethod method, Value base, List<Value> args) {
		List<SymbolicValue> result = this.processRecognition(method, base, args);

		if(result != null && !result.isEmpty()) {
			return result;
		}
		if(this.next != null) {
			return this.next.recognize(method, base, args);
		}
		else {
			return null;
		}
	}

	protected void addSimpleResult(Value effectiveArg, List<SymbolicValue> results) {
		ContextualValues contextualValues = this.se.getContext().get(effectiveArg);
		List<SymbolicValue> values = null;
		if(contextualValues == null) {
			results.add(new UnknownValue());
		}else {
			values = contextualValues.getLastCoherentValues();
			for(SymbolicValue sv : values) {
				results.add(sv);
			}
		}
	}
}