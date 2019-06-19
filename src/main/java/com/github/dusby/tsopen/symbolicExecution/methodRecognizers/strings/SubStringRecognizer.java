package com.github.dusby.tsopen.symbolicExecution.methodRecognizers.strings;

import java.util.ArrayList;
import java.util.List;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecutioner;

import soot.SootMethod;
import soot.Value;
import soot.jimple.IntConstant;
import soot.jimple.StringConstant;

public class SubStringRecognizer extends StringMethodsRecognizerProcessor {

	public SubStringRecognizer(StringMethodsRecognizerProcessor next, SymbolicExecutioner se) {
		super(next, se);
	}

	@Override
	public List<String> processRecognition(SootMethod method, Value base, List<Value> args) {
		List<String> results = new ArrayList<String>();
		StringConstant baseStr = null;
		Value arg1 = null,
				arg2 = null;
		int v1 = 0,
				v2 = 0;
		if(method.getName().equals(SUBSTRING)) {
			if(base instanceof StringConstant) {
				baseStr = (StringConstant) base;
				if(baseStr.value.contains(UNKNOWN_STRING)){
					results.add(UNKNOWN_STRING);
				}else {
					arg1 = args.get(0);
					if(arg1 instanceof IntConstant) {
						v1 = ((IntConstant)arg1).value;
						if(args.size() == 1) {
							results.add(baseStr.value.substring(v1));
						}else {
							arg2 = args.get(1);
							if(arg2 instanceof IntConstant) {
								v2 = ((IntConstant)arg2).value;
								results.add(baseStr.value.substring(v1, v2));
							}
						}
					}
				}
			}
			return results;
		}
		return null;
	}

}
