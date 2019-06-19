package com.github.dusby.tsopen.symbolicExecution.methodRecognizers.strings;

import java.util.ArrayList;
import java.util.List;

import com.github.dusby.tsopen.symbolicExecution.ContextualValues;
import com.github.dusby.tsopen.symbolicExecution.SymbolicExecutioner;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.ConcreteValue;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.MethodRepresentationValue;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValueProvider;

import soot.SootMethod;
import soot.Value;
import soot.jimple.Constant;
import soot.jimple.StringConstant;

public class AppendRecognizer extends StringMethodsRecognizerProcessor {

	public AppendRecognizer(StringMethodsRecognizerProcessor next, SymbolicExecutioner se) {
		super(next, se);
	}

	@Override
	public List<SymbolicValueProvider> processRecognition(SootMethod method, Value base, List<Value> args) {
		List<SymbolicValueProvider> values = null;
		List<SymbolicValueProvider> results = new ArrayList<SymbolicValueProvider>();
		ContextualValues contextualValuesOfBase = null;
		if(method.getName().equals(APPEND)) {
			contextualValuesOfBase = this.se.getContext().get(base);
			if(contextualValuesOfBase == null) {
				results.addAll(this.computeValue(new SymbolicValue(), args, base, method));
			}else {
				values = contextualValuesOfBase.getLastCoherentValues();
				for(SymbolicValueProvider svp : values) {
					results.addAll(this.computeValue(svp, args, base, method));
				}
			}
			return results;
		}
		return null;
	}

	private List<SymbolicValueProvider> computeValue(SymbolicValueProvider symVal, List<Value> args, Value base, SootMethod method) {
		List<SymbolicValueProvider> results = new ArrayList<SymbolicValueProvider>();
		Value effectiveArg = args.get(0);
		ContextualValues contextualValuesOfBase = null;
		List<SymbolicValueProvider> values = null;
		if(effectiveArg instanceof Constant) {
			if(symVal.isConcrete()) {
				results.add(new ConcreteValue(StringConstant.v(String.format("%s%s", symVal, effectiveArg))));
			}else {
				results.add(new MethodRepresentationValue(base, args, method, this.se));
			}
		}else {
			contextualValuesOfBase = this.se.getContext().get(effectiveArg);
			if(contextualValuesOfBase == null) {
				if(symVal.isConcrete()) {
					results.add(new ConcreteValue(StringConstant.v(String.format("%s%s", symVal, UNKNOWN_STRING))));
				}else {
					results.add(new MethodRepresentationValue(base, args, method, this.se));
				}
			}else {
				values = contextualValuesOfBase.getLastCoherentValues();
				for(SymbolicValueProvider svp : values) {
					if(symVal.isConcrete() && svp.isConcrete()) {
						results.add(new ConcreteValue(StringConstant.v(String.format("%s%s", symVal, svp))));
					}else {
						results.add(new MethodRepresentationValue(base, args, method, this.se));
					}
				}
			}
		}
		return results;
	}
}
