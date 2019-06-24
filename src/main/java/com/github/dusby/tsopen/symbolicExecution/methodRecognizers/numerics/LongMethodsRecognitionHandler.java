package com.github.dusby.tsopen.symbolicExecution.methodRecognizers.numerics;

import java.util.List;

import com.github.dusby.tsopen.symbolicExecution.ContextualValues;
import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;

import soot.SootMethod;
import soot.Value;

public abstract class LongMethodsRecognitionHandler implements LongMethodsRecognition {

	private LongMethodsRecognitionHandler next;
	protected SymbolicExecution se;

	public LongMethodsRecognitionHandler(LongMethodsRecognitionHandler next, SymbolicExecution se) {
		this.next = next;
		this.se = se;
	}

	@Override
	public List<SymbolicValue> recognizeLongMethod(SootMethod method, Value base, List<Value> args, SymbolicValue sv) {
		List<SymbolicValue> result = this.processLongMethod(method, base, args, sv);

		if(result != null && !result.isEmpty()) {
			return result;
		}
		if(this.next != null) {
			return this.next.recognizeLongMethod(method, base, args, sv);
		}
		else {
			return null;
		}
	}

	@Override
	public boolean containsTag(Value base, String nowTag) {
		List<SymbolicValue> values = null;
		ContextualValues contextualValues = null;
		if(base != null) {
			contextualValues = this.se.getContext().get(base);
			if(contextualValues != null) {
				values = contextualValues.getLastCoherentValues();
				for(SymbolicValue sv : values) {
					if(sv.containsTag(nowTag)) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
