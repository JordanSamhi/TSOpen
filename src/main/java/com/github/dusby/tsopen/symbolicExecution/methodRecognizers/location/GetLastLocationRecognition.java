package com.github.dusby.tsopen.symbolicExecution.methodRecognizers.location;

import java.util.List;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import com.github.dusby.tsopen.utils.Constants;

import soot.SootMethod;
import soot.Type;
import soot.Value;
import soot.tagkit.StringConstantValueTag;

public class GetLastLocationRecognition extends LocationMethodsRecognitionHandler {

	public GetLastLocationRecognition(LocationMethodsRecognitionHandler next, SymbolicExecution se) {
		super(next, se);
	}

	@Override
	public boolean processLocationMethod(SootMethod method, List<Value> args, SymbolicValue sv) {
		String methodName = method.getName();
		Value base = sv.getBase();
		Type type = base.getType();
		if((base != null && type.toString().equals(Constants.COM_GOOGLE_ANDROID_GMS_LOCATION_LOCATION_RESULT) && methodName.equals(Constants.GET_LAST_LOCATION))) {
			sv.addTag(new StringConstantValueTag(Constants.HERE_TAG));
			return true;
		}
		return false;
	}
}
