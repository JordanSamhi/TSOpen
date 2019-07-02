package com.github.dusby.tsopen.symbolicExecution.methodRecognizers.bool;

import java.util.List;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import com.github.dusby.tsopen.utils.Constants;
import com.github.dusby.tsopen.utils.Utils;

import soot.SootMethod;
import soot.Value;
import soot.jimple.Constant;
import soot.tagkit.StringConstantValueTag;

public class StartsWithRecognition extends BooleanMethodsRecognitionHandler {

	public StartsWithRecognition(BooleanMethodsRecognitionHandler next, SymbolicExecution se) {
		super(next, se);
	}
	//TODO toLowerCase and so on...
	@Override
	public boolean processBooleanMethod(SootMethod method, Value base, SymbolicValue sv, List<Value> args) {
		Value firstArg = null;
		String methodName = method.getName();
		if(methodName.equals(Constants.STARTS_WITH)) {
			firstArg = args.get(0);
			if(Utils.containsTag(base, Constants.SMS_BODY_TAG, this.se) || Utils.containsTag(base, Constants.SMS_SENDER_TAG, this.se)) {
				if(firstArg instanceof Constant) {
					sv.addTag(new StringConstantValueTag(Constants.SUSPICIOUS));
					return true;
				}
			}
		}
		return false;
	}
}
