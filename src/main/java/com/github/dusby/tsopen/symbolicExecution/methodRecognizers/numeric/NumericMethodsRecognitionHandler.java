package com.github.dusby.tsopen.symbolicExecution.methodRecognizers.numeric;

import java.util.List;

import com.github.dusby.tsopen.symbolicExecution.ContextualValues;
import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;

import soot.SootMethod;
import soot.Value;

public abstract class NumericMethodsRecognitionHandler implements NumericMethodsRecognition {

	private NumericMethodsRecognitionHandler next;
	protected SymbolicExecution se;

	public NumericMethodsRecognitionHandler(NumericMethodsRecognitionHandler next, SymbolicExecution se) {
		this.next = next;
		this.se = se;
	}

	@Override
	public boolean recognizeNumericMethod(SootMethod method, Value base, List<Value> args, SymbolicValue sv) {
		boolean recognized = this.processNumericMethod(method, base, args, sv);

		if(recognized) {
			return recognized;
		}
		if(this.next != null) {
			return this.next.recognizeNumericMethod(method, base, args, sv);
		}
		else {
			return false;
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
