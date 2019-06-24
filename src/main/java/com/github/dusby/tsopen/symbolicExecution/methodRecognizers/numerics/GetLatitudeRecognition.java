package com.github.dusby.tsopen.symbolicExecution.methodRecognizers.numerics;

import java.util.ArrayList;
import java.util.List;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import com.github.dusby.tsopen.utils.Constants;

import soot.SootClass;
import soot.SootMethod;
import soot.Value;
import soot.tagkit.StringConstantValueTag;

public class GetLatitudeRecognition extends LongMethodsRecognitionHandler {

	public GetLatitudeRecognition(LongMethodsRecognitionHandler next, SymbolicExecution se, SymbolicValue sv) {
		super(next, se);
	}

	@Override
	public List<SymbolicValue> processLongMethod(SootMethod method, Value base, List<Value> args, SymbolicValue sv) {
		SootClass declaringClass = method.getDeclaringClass();
		String methodName = method.getName();
		List<SymbolicValue> results = new ArrayList<SymbolicValue>();
		if(this.containsTag(base, Constants.HERE_TAG)) {
			if(declaringClass.getName().equals(Constants.ANDROID_LOCATION_LOCATION) && methodName.equals(Constants.GET_LATITUDE)) {
				sv.addTag(new StringConstantValueTag(Constants.LATITUDE_TAG));
			}
		}
		return results;
	}

}