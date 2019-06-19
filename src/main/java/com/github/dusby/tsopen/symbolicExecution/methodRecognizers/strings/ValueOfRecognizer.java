package com.github.dusby.tsopen.symbolicExecution.methodRecognizers.strings;

import java.util.ArrayList;
import java.util.List;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecutioner;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.ConcreteValue;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValueProvider;

import soot.SootMethod;
import soot.Value;
import soot.jimple.Constant;

public class ValueOfRecognizer extends StringMethodsRecognizerProcessor {

	public ValueOfRecognizer(StringMethodsRecognizerProcessor next, SymbolicExecutioner se) {
		super(next, se);
	}

	@Override
	public List<SymbolicValueProvider> processRecognition(SootMethod method, Value base, List<Value> args) {
		List<SymbolicValueProvider> results = new ArrayList<SymbolicValueProvider>();
		Value effectiveArg = null;
		if(method.getName().equals(VALUEOF)) {
			effectiveArg = args.get(0);
			if(effectiveArg instanceof Constant) {
				results.add(new ConcreteValue((Constant)effectiveArg));
			}else {
				this.addSimpleResult(effectiveArg, results);
			}
			return results;
		}
		return null;
	}
}
