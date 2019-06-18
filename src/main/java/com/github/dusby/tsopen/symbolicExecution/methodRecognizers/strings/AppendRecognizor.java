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

public class AppendRecognizor extends StringMethodsRecognizerProcessor {

	public AppendRecognizor(StringMethodsRecognizerProcessor next, SymbolicExecutioner se) {
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
				values = contextualValuesOfBase.getLastValues();
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
		Value effectifArg = args.get(0);
		ContextualValues contextualValuesOfBase = null;
		List<SymbolicValueProvider> values = null;
		if(effectifArg instanceof Constant) {
			if(effectifArg instanceof StringConstant) {
				results.add(String.format("%s%s", baseStr, ((StringConstant)effectifArg).value));
			}else if(effectifArg instanceof IntConstant) {
				results.add(String.format("%s%s", baseStr, ((IntConstant)effectifArg).value));
			}else if(effectifArg instanceof LongConstant) {
				results.add(String.format("%s%s", baseStr, ((LongConstant)effectifArg).value));
			}else if(effectifArg instanceof DoubleConstant) {
				results.add(String.format("%s%s", baseStr, ((DoubleConstant)effectifArg).value));
			}else if(effectifArg instanceof FloatConstant) {
				results.add(String.format("%s%s", baseStr, ((FloatConstant)effectifArg).value));
			}else if(effectifArg instanceof NullConstant) {
				results.add(String.format("%s%s", baseStr, NULL));
			}
		}else {
			contextualValuesOfBase = this.se.getContext().get(effectifArg);
			if(contextualValuesOfBase == null) {
				results.add(String.format("%s_%s", baseStr, UNKNOWN_STRING));
			}else {
				values = contextualValuesOfBase.getLastValues();
				for(SymbolicValueProvider svp : values) {
					results.add(String.format("%s%s", baseStr, svp.getContextValue()));
				}
			}
		}
		return results;
	}
}
