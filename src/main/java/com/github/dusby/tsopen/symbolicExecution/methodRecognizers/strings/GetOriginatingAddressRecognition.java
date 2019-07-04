package com.github.dusby.tsopen.symbolicExecution.methodRecognizers.strings;

import java.util.ArrayList;
import java.util.List;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.MethodRepresentationValue;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import com.github.dusby.tsopen.utils.Constants;

import soot.SootMethod;
import soot.Value;
import soot.tagkit.StringConstantValueTag;

public class GetOriginatingAddressRecognition extends StringMethodsRecognitionHandler {

	public GetOriginatingAddressRecognition(StringMethodsRecognitionHandler next, SymbolicExecution se) {
		super(next, se);
	}

	@Override
	public List<SymbolicValue> processStringMethod(SootMethod method, Value base, List<Value> args) {
		List<SymbolicValue> results = new ArrayList<SymbolicValue>();
		MethodRepresentationValue mrv = new MethodRepresentationValue(base, args, method, this.se);
		if(method.getName().equals(Constants.GET_ORIGINATING_ADDRESS) || method.getName().equals(Constants.GET_DISPLAY_ORIGINATING_ADDRESS)) {
			mrv.addTag(new StringConstantValueTag(Constants.SMS_BODY_TAG));
			results.add(mrv);
		}
		return results;
	}

}
