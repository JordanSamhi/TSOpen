package com.github.JordanSamhi.tsopen.symbolicExecution.methodRecognizers.dateTime;

import com.github.JordanSamhi.tsopen.symbolicExecution.SymbolicExecution;
import com.github.JordanSamhi.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import com.github.JordanSamhi.tsopen.utils.Constants;

import soot.SootMethod;
import soot.tagkit.StringConstantValueTag;

public class SetToNowRecognition extends DateTimeMethodsRecognitionHandler {

	public SetToNowRecognition(DateTimeMethodsRecognitionHandler next, SymbolicExecution se) {
		super(next, se);
	}

	@Override
	public boolean processDateTimeMethod(SootMethod method, SymbolicValue sv) {
		String className = method.getDeclaringClass().getName();
		String methodName = method.getName();
		if(methodName.equals(Constants.SET_TO_NOW) && (className.equals(Constants.ANDROID_TEXT_FORMAT_TIME))) {
			sv.addTag(new StringConstantValueTag(Constants.NOW_TAG));
			return true;
		}
		return false;
	}

}
