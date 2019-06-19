package com.github.dusby.tsopen.symbolicExecution.methodRecognizers.strings;

import java.util.ArrayList;
import java.util.List;

import com.github.dusby.tsopen.symbolicExecution.ContextualValues;
import com.github.dusby.tsopen.symbolicExecution.SymbolicExecutioner;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValueProvider;

import soot.SootMethod;
import soot.Value;
import soot.jimple.Constant;
import soot.jimple.DoubleConstant;
import soot.jimple.FloatConstant;
import soot.jimple.IntConstant;
import soot.jimple.LongConstant;
import soot.jimple.NullConstant;
import soot.jimple.StringConstant;

public class ValueOfRecognizer extends StringMethodsRecognizerProcessor {

	public ValueOfRecognizer(StringMethodsRecognizerProcessor next, SymbolicExecutioner se) {
		super(next, se);
	}

	@Override
	public List<String> processRecognition(SootMethod method, Value base, List<Value> args) {
		List<SymbolicValueProvider> values = null;
		List<String> results = new ArrayList<String>();
		ContextualValues contextualValues = null;
		Value effectiveArg = null;
		if(method.getName().equals(VALUEOF)) {
			effectiveArg = args.get(0);
			if(effectiveArg instanceof Constant) {
				if(effectiveArg instanceof StringConstant) {
					results.add(((StringConstant)effectiveArg).value);
				}else if(effectiveArg instanceof IntConstant) {
					results.add(String.valueOf(((IntConstant)effectiveArg).value));
				}else if(effectiveArg instanceof LongConstant) {
					results.add(String.valueOf(((LongConstant)effectiveArg).value));
				}else if(effectiveArg instanceof DoubleConstant) {
					results.add(String.valueOf(((DoubleConstant)effectiveArg).value));
				}else if(effectiveArg instanceof FloatConstant) {
					results.add(String.valueOf(((FloatConstant)effectiveArg).value));
				}else if(effectiveArg instanceof NullConstant) {
					results.add(NULL);
				}
			}else {
				//TODO Factorize this in other recognizers
				contextualValues = this.se.getContext().get(effectiveArg);
				if(contextualValues == null) {
					results.add(UNKNOWN_STRING);
				}else {
					values = contextualValues.getLastCoherentValues();
					for(SymbolicValueProvider svp : values) {
						results.add(svp.getContextValue());
					}
				}
			}
			return results;
		}
		return null;
	}

}
