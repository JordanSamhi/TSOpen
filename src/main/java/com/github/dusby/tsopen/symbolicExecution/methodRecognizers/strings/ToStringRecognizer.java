package com.github.dusby.tsopen.symbolicExecution.methodRecognizers.strings;

import java.util.ArrayList;
import java.util.List;

import com.github.dusby.tsopen.symbolicExecution.ContextualValues;
import com.github.dusby.tsopen.symbolicExecution.SymbolicExecutioner;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValueProvider;

import soot.SootMethod;
import soot.Value;

public class ToStringRecognizer extends StringMethodsRecognizerProcessor {

	public ToStringRecognizer(StringMethodsRecognizerProcessor next, SymbolicExecutioner se) {
		super(next, se);
	}

	@Override
	public List<String> processRecognition(SootMethod method, Value base, List<Value> args) {
		ContextualValues contextualValues = null;
		List<String> results = new ArrayList<String>();
		List<SymbolicValueProvider> values = null;
		if(method.getName().equals(TOSTRING)) {
			contextualValues = this.se.getContext().get(base);
			if(contextualValues == null) {
				results.add(base.getType().toString());
			}else {
				values = contextualValues.getLastCoherentValues();
				for(SymbolicValueProvider svp : values) {
					results.add(svp.getContextValue());
				}
			}
			return results;
		}
		return null;
	}

}
