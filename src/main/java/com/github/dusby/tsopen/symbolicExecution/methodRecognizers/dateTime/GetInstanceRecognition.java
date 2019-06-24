package com.github.dusby.tsopen.symbolicExecution.methodRecognizers.dateTime;

import java.util.List;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import com.github.dusby.tsopen.utils.Constants;

import soot.SootMethod;
import soot.Value;
import soot.tagkit.StringConstantValueTag;

public class GetInstanceRecognition extends DateTimeMethodsRecognitionHandler {

	public GetInstanceRecognition(DateTimeMethodsRecognitionHandler next, SymbolicExecution se) {
		super(next, se);
	}

	@Override
	public boolean processDateTimeMethod(SootMethod method, List<Value> args, SymbolicValue sv) {
		String className = method.getDeclaringClass().getName();
		String methodName = method.getName();
		if((methodName.equals(Constants.GET_INSTANCE) && (className.equals(Constants.JAVA_UTIL_CALENDAR) || className.equals(Constants.JAVA_UTIL_GREGORIAN_CALENDAR)))) {
			sv.addTag(new StringConstantValueTag(Constants.NOW_TAG));
			return true;
		}
		return false;
	}

}