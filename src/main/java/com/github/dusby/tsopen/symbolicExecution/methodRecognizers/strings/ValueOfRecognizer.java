package com.github.dusby.tsopen.symbolicExecution.methodRecognizers.strings;

import java.util.ArrayList;
import java.util.List;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.ConstantValue;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;

import soot.SootMethod;
import soot.Value;
import soot.jimple.Constant;

public class ValueOfRecognizer extends StringMethodsRecognizerHandler {

	public ValueOfRecognizer(StringMethodsRecognizerHandler next, SymbolicExecution se) {
		super(next, se);
	}

	@Override
	public List<SymbolicValue> processRecognition(SootMethod method, Value base, List<Value> args) {
		List<SymbolicValue> results = new ArrayList<SymbolicValue>();
		Value effectiveArg = null;
		if(method.getName().equals(VALUEOF)) {
			effectiveArg = args.get(0);
			if(effectiveArg instanceof Constant) {
				results.add(new ConstantValue((Constant)effectiveArg));
			}else {
				this.addSimpleResult(effectiveArg, results);
			}
			return results;
		}
		return null;
	}
}
