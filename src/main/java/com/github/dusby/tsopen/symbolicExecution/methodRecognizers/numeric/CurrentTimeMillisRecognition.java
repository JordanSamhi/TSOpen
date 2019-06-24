package com.github.dusby.tsopen.symbolicExecution.methodRecognizers.numeric;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import com.github.dusby.tsopen.utils.Constants;

import soot.SootClass;
import soot.SootMethod;
import soot.Value;
import soot.tagkit.StringConstantValueTag;

public class CurrentTimeMillisRecognition extends NumericMethodsRecognitionHandler {

	public CurrentTimeMillisRecognition(NumericMethodsRecognitionHandler next, SymbolicExecution se) {
		super(next, se);
	}

	@Override
	public boolean processNumericMethod(SootMethod method, Value base, SymbolicValue sv) {
		SootClass declaringClass = method.getDeclaringClass();
		String methodName = method.getName();
		if (declaringClass.getName().equals(Constants.JAVA_LANG_SYSTEM) && methodName.equals(Constants.CURRENT_TIME_MILLIS)) {
			sv.addTag(new StringConstantValueTag(Constants.NOW_TAG));
			return true;
		}
		return false;
	}

}
