package com.github.dusby.tsopen.symbolicExecution.methodRecognizers.numeric;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import com.github.dusby.tsopen.utils.Constants;
import com.github.dusby.tsopen.utils.Utils;

import soot.SootClass;
import soot.SootMethod;
import soot.Value;
import soot.tagkit.StringConstantValueTag;

public class GetSecondsRecognition extends NumericMethodsRecognitionHandler {

	public GetSecondsRecognition(NumericMethodsRecognitionHandler next, SymbolicExecution se) {
		super(next, se);
	}

	@Override
	public boolean processNumericMethod(SootMethod method, Value base, SymbolicValue sv) {
		SootClass declaringClass = method.getDeclaringClass();
		String methodName = method.getName();
		if (declaringClass.getName().equals(Constants.JAVA_UTIL_DATE) && methodName.equals(Constants.GET_SECONDS)) {
			if(Utils.containsTag(base, Constants.NOW_TAG, this.se)) {
				sv.addTag(new StringConstantValueTag(Constants.SECONDS_TAG));
				return true;
			}
		}
		return false;
	}

}
