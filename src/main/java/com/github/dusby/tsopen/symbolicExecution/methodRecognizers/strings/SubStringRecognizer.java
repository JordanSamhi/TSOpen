package com.github.dusby.tsopen.symbolicExecution.methodRecognizers.strings;

import java.util.ArrayList;
import java.util.List;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecutioner;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.ConstantValue;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.MethodRepresentationValue;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;

import soot.SootMethod;
import soot.Value;
import soot.jimple.IntConstant;
import soot.jimple.StringConstant;

public class SubStringRecognizer extends StringMethodsRecognizerProcessor {

	public SubStringRecognizer(StringMethodsRecognizerProcessor next, SymbolicExecutioner se) {
		super(next, se);
	}

	@Override
	public List<SymbolicValue> processRecognition(SootMethod method, Value base, List<Value> args) {
		List<SymbolicValue> results = new ArrayList<SymbolicValue>();
		StringConstant baseStr = null;
		Value arg1 = null,
				arg2 = null;
		int v1 = 0,
				v2 = 0;
		if(method.getName().equals(SUBSTRING)) {
			if(base instanceof StringConstant) {
				baseStr = (StringConstant) base;
				if(baseStr.value.contains(UNKNOWN_STRING)){
					results.add(new MethodRepresentationValue(base, args, method, this.se));
				}else {
					arg1 = args.get(0);
					if(arg1 instanceof IntConstant) {
						v1 = ((IntConstant)arg1).value;
						if(args.size() == 1) {
							results.add(new ConstantValue(StringConstant.v(baseStr.value.substring(v1))));
						}else {
							arg2 = args.get(1);
							if(arg2 instanceof IntConstant) {
								v2 = ((IntConstant)arg2).value;
								results.add(new ConstantValue(StringConstant.v(baseStr.value.substring(v1, v2))));
							}
						}
					}
				}
			}else {
				results.add(new MethodRepresentationValue(base, args, method, this.se));
			}
			return results;
		}
		return null;
	}

}
