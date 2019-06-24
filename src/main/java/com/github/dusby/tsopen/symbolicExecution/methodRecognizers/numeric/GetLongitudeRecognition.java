package com.github.dusby.tsopen.symbolicExecution.methodRecognizers.numeric;

import java.util.List;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import com.github.dusby.tsopen.utils.Constants;

import soot.SootClass;
import soot.SootMethod;
import soot.Value;
import soot.tagkit.StringConstantValueTag;

public class GetLongitudeRecognition extends NumericMethodsRecognitionHandler {

	public GetLongitudeRecognition(NumericMethodsRecognitionHandler next, SymbolicExecution se) {
		super(next, se);
	}

	@Override
	public boolean processLongMethod(SootMethod method, Value base, List<Value> args, SymbolicValue sv) {
		SootClass declaringClass = method.getDeclaringClass();
		String methodName = method.getName();
		if(this.containsTag(base, Constants.HERE_TAG)) {
			if(declaringClass.getName().equals(Constants.ANDROID_LOCATION_LOCATION) && methodName.equals(Constants.GET_LONGITUDE)) {
				sv.addTag(new StringConstantValueTag(Constants.LONGITUDE_TAG));
				return true;
			}
		}
		return false;
	}

}
