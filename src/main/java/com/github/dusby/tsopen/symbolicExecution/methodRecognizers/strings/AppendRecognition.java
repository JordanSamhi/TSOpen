package com.github.dusby.tsopen.symbolicExecution.methodRecognizers.strings;

import java.util.ArrayList;
import java.util.List;

import com.github.dusby.tsopen.symbolicExecution.ContextualValues;
import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.ConstantValue;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.MethodRepresentationValue;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.UnknownValue;
import com.github.dusby.tsopen.utils.Constants;

import soot.SootMethod;
import soot.Value;
import soot.jimple.Constant;
import soot.jimple.StringConstant;

public class AppendRecognition extends StringMethodsRecognitionHandler {

	public AppendRecognition(StringMethodsRecognitionHandler next, SymbolicExecution se) {
		super(next, se);
	}

	@Override
	public List<SymbolicValue> processStringMethod(SootMethod method, Value base, List<Value> args) {
		List<SymbolicValue> values = null;
		List<SymbolicValue> results = new ArrayList<SymbolicValue>();
		ContextualValues contextualValuesOfBase = null;
		if(method.getName().equals(Constants.APPEND)) {
			contextualValuesOfBase = this.se.getContext().get(base);
			if(contextualValuesOfBase == null) {
				results.addAll(this.computeValue(new UnknownValue(), args, base, method));
			}else {
				values = contextualValuesOfBase.getLastCoherentValues();
				for(SymbolicValue sv : values) {
					results.addAll(this.computeValue(sv, args, base, method));
				}
			}
			return results;
		}
		return null;
	}

	private List<SymbolicValue> computeValue(SymbolicValue symVal, List<Value> args, Value base, SootMethod method) {
		List<SymbolicValue> results = new ArrayList<SymbolicValue>();
		Value effectiveArg = args.get(0);
		ContextualValues contextualValuesOfBase = null;
		List<SymbolicValue> values = null;
		if(effectiveArg instanceof Constant) {
			if(symVal.isConstant()) {
				results.add(new ConstantValue(StringConstant.v(String.format("%s%s", symVal, effectiveArg))));
			}else {
				results.add(new MethodRepresentationValue(base, args, method, this.se));
			}
		}else {
			contextualValuesOfBase = this.se.getContext().get(effectiveArg);
			if(contextualValuesOfBase == null) {
				if(symVal.isConstant()) {
					results.add(new ConstantValue(StringConstant.v(String.format("%s%s", symVal, Constants.UNKNOWN_STRING))));
				}else {
					results.add(new MethodRepresentationValue(base, args, method, this.se));
				}
			}else {
				values = contextualValuesOfBase.getLastCoherentValues();
				for(SymbolicValue sv : values) {
					if(symVal.isConstant() && sv.isConstant()) {
						results.add(new ConstantValue(StringConstant.v(String.format("%s%s", symVal, sv))));
					}else {
						results.add(new MethodRepresentationValue(base, args, method, this.se));
					}
				}
			}
		}
		return results;
	}
}
