package com.github.JordanSamhi.tsopen.symbolicExecution.methodRecognizers.dateTime;

import com.github.JordanSamhi.tsopen.symbolicExecution.SymbolicExecution;
import com.github.JordanSamhi.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import com.github.JordanSamhi.tsopen.utils.Constants;

import soot.SootMethod;
import soot.tagkit.StringConstantValueTag;

public class GetInstanceRecognition extends DateTimeMethodsRecognitionHandler {

	public GetInstanceRecognition(DateTimeMethodsRecognitionHandler next, SymbolicExecution se) {
		super(next, se);
	}

	@Override
	public boolean processDateTimeMethod(SootMethod method, SymbolicValue sv) {
		String className = method.getDeclaringClass().getName();
		String methodName = method.getName();
		if((methodName.equals(Constants.GET_INSTANCE) && (className.equals(Constants.JAVA_UTIL_CALENDAR) || className.equals(Constants.JAVA_UTIL_GREGORIAN_CALENDAR)))) {
			sv.addTag(new StringConstantValueTag(Constants.NOW_TAG));
			return true;
		}
		return false;
	}

}
