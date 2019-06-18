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

public class AppendRecognizer extends StringMethodsRecognizerProcessor {

	public AppendRecognizer(StringMethodsRecognizerProcessor next, SymbolicExecutioner se) {
		super(next, se);
	}

	@Override
	public List<String> processRecognition(SootMethod method, Value base, List<Value> args) {
		String baseStr = null;
		List<SymbolicValueProvider> values = null;
		List<String> results = new ArrayList<String>();
		ContextualValues contextualValuesOfBase = null;
		if(method.getName().equals(APPEND)) {
			contextualValuesOfBase = this.se.getContext().get(base);
			if(contextualValuesOfBase == null) {
				baseStr = UNKNOWN_STRING;
				results.addAll(this.computeValue(baseStr, args));
			}else {
				values = contextualValuesOfBase.getLastCoherentValues();
				for(SymbolicValueProvider svp : values) {
					baseStr = svp.getContextValue();
					results.addAll(this.computeValue(baseStr, args));
				}
			}
			return results;
		}
		return null;
	}

	private List<String> computeValue(String baseStr, List<Value> args) {
		List<String> results = new ArrayList<String>();
		Value effectiveArg = args.get(0);
		ContextualValues contextualValuesOfBase = null;
		List<SymbolicValueProvider> values = null;
		if(effectiveArg instanceof Constant) {
			if(effectiveArg instanceof StringConstant) {
				results.add(String.format("%s%s", baseStr, ((StringConstant)effectiveArg).value));
			}else if(effectiveArg instanceof IntConstant) {
				results.add(String.format("%s%s", baseStr, ((IntConstant)effectiveArg).value));
			}else if(effectiveArg instanceof LongConstant) {
				results.add(String.format("%s%s", baseStr, ((LongConstant)effectiveArg).value));
			}else if(effectiveArg instanceof DoubleConstant) {
				results.add(String.format("%s%s", baseStr, ((DoubleConstant)effectiveArg).value));
			}else if(effectiveArg instanceof FloatConstant) {
				results.add(String.format("%s%s", baseStr, ((FloatConstant)effectiveArg).value));
			}else if(effectiveArg instanceof NullConstant) {
				results.add(String.format("%s%s", baseStr, NULL));
			}
		}else {
			contextualValuesOfBase = this.se.getContext().get(effectiveArg);
			if(contextualValuesOfBase == null) {
				results.add(String.format("%s%s", baseStr, UNKNOWN_STRING));
			}else {
				values = contextualValuesOfBase.getLastCoherentValues();
				for(SymbolicValueProvider svp : values) {
					results.add(String.format("%s%s", baseStr, svp.getContextValue()));
				}
			}
		}
		return results;
	}
}
