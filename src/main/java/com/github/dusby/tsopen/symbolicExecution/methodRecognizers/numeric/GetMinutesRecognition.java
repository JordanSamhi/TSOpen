package com.github.dusby.tsopen.symbolicExecution.methodRecognizers.numeric;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import com.github.dusby.tsopen.utils.Constants;

import soot.SootClass;
import soot.SootMethod;
import soot.Value;
import soot.tagkit.StringConstantValueTag;

public class GetMinutesRecognition extends NumericMethodsRecognitionHandler {

	public GetMinutesRecognition(NumericMethodsRecognitionHandler next, SymbolicExecution se) {
		super(next, se);
	}

	@Override
	public boolean processNumericMethod(SootMethod method, Value base, SymbolicValue sv) {
		SootClass declaringClass = method.getDeclaringClass();
		String methodName = method.getName();
		if(this.containsTag(base, Constants.NOW_TAG)) {
			if(declaringClass.getName().equals(Constants.JAVA_UTIL_DATE) && methodName.equals(Constants.GET_MINUTES)) {
				sv.addTag(new StringConstantValueTag(Constants.MINUTES_TAG));
				return true;
			}
		}
		return false;
	}

}
