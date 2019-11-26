package com.github.JordanSamhi.tsopen.symbolicExecution.methodRecognizers.strings;

import java.util.ArrayList;
import java.util.List;

import com.github.JordanSamhi.tsopen.symbolicExecution.SymbolicExecution;
import com.github.JordanSamhi.tsopen.symbolicExecution.symbolicValues.ConstantValue;
import com.github.JordanSamhi.tsopen.symbolicExecution.symbolicValues.MethodRepresentationValue;
import com.github.JordanSamhi.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import com.github.JordanSamhi.tsopen.utils.Constants;

import soot.SootMethod;
import soot.Value;
import soot.jimple.IntConstant;
import soot.jimple.StringConstant;

public class SubStringRecognition extends StringMethodsRecognitionHandler {

	public SubStringRecognition(StringMethodsRecognitionHandler next, SymbolicExecution se) {
		super(next, se);
	}

	@Override
	public List<SymbolicValue> processStringMethod(SootMethod method, Value base, List<Value> args) {
		List<SymbolicValue> results = new ArrayList<SymbolicValue>();
		StringConstant baseStr = null;
		Value arg1 = null,
				arg2 = null;
		int v1 = 0,
				v2 = 0;
		SymbolicValue object = null;
		if(method.getName().equals(Constants.SUBSTRING)) {
			if(base instanceof StringConstant) {
				baseStr = (StringConstant) base;
				if(baseStr.value.contains(Constants.UNKNOWN_STRING)){
					object = new MethodRepresentationValue(base, args, method, this.se);
				}else {
					arg1 = args.get(0);
					if(arg1 instanceof IntConstant) {
						v1 = ((IntConstant)arg1).value;
						if(args.size() == 1) {
							object = new ConstantValue(StringConstant.v(baseStr.value.substring(v1)), this.se);
						}else {
							arg2 = args.get(1);
							if(arg2 instanceof IntConstant) {
								v2 = ((IntConstant)arg2).value;
								object = new ConstantValue(StringConstant.v(baseStr.value.substring(v1, v2)), this.se);
							}
						}
					}
				}
			}else {
				object = new MethodRepresentationValue(base, args, method, this.se);
			}
			if(object != null) {
				this.addResult(results, object);
			}
			return results;
		}
		return null;
	}

}
