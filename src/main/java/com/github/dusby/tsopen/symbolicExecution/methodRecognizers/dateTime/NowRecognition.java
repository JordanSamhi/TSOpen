package com.github.dusby.tsopen.symbolicExecution.methodRecognizers.dateTime;

import java.util.List;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import com.github.dusby.tsopen.utils.Constants;

import soot.SootMethod;
import soot.Value;
import soot.tagkit.StringConstantValueTag;

public class NowRecognition extends DateTimeMethodsRecognitionHandler {

	public NowRecognition(DateTimeMethodsRecognitionHandler next, SymbolicExecution se) {
		super(next, se);
	}

	@Override
	public boolean processDateTimeMethod(SootMethod method, List<Value> args, SymbolicValue sv) {
		String className = method.getDeclaringClass().getName();
		String methodName = method.getName();
		if(methodName.equals(Constants.NOW) && (className.equals(Constants.JAVA_TIME_LOCAL_DATE_TIME) || className.equals(Constants.JAVA_TIME_LOCAL_DATE))) {
			sv.addTag(new StringConstantValueTag(Constants.NOW_TAG));
			return true;
		}
		return false;
	}

}