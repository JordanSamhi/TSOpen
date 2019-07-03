package com.github.dusby.tsopen.symbolicExecution.methodRecognizers.bool;

import java.util.List;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import com.github.dusby.tsopen.utils.Constants;
import com.github.dusby.tsopen.utils.Utils;

import soot.SootMethod;
import soot.Value;
import soot.tagkit.StringConstantValueTag;

public class AfterRecognition extends BooleanMethodsRecognitionHandler {

	public AfterRecognition(BooleanMethodsRecognitionHandler next, SymbolicExecution se) {
		super(next, se);
	}

	@Override
	public boolean processBooleanMethod(SootMethod method, Value base, SymbolicValue sv, List<Value> args) {
		Value firstArg = null;
		String methodName = method.getName();
		if(methodName.equals(Constants.AFTER)) {
			firstArg = args.get(0);
			if(Utils.containsTag(base, Constants.NOW_TAG, this.se)) {
				if(firstArg.getType().toString().equals(Constants.JAVA_UTIL_DATE)
						|| firstArg.getType().toString().equals(Constants.JAVA_UTIL_CALENDAR)
						|| firstArg.getType().toString().equals(Constants.JAVA_UTIL_GREGORIAN_CALENDAR)
						|| firstArg.getType().toString().equals(Constants.JAVA_TEXT_SIMPLE_DATE_FORMAT)
						|| firstArg.getType().toString().equals(Constants.JAVA_TIME_LOCAL_DATE_TIME)
						|| firstArg.getType().toString().equals(Constants.JAVA_TIME_LOCAL_DATE)) {
					sv.addTag(new StringConstantValueTag(Constants.SUSPICIOUS));
					return true;
				}
			}
		}
		return false;
	}

}
