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

public class GetMessageBodyRecognition extends StringMethodsRecognitionHandler {

	public GetMessageBodyRecognition(StringMethodsRecognitionHandler next, SymbolicExecution se) {
		super(next, se);
	}

	@Override
	public List<SymbolicValue> processStringMethod(SootMethod method, Value base, List<Value> args) {
		List<SymbolicValue> results = new ArrayList<SymbolicValue>();
		StringConstantValueTag scvt = null;
		MethodRepresentationValue mrv = new MethodRepresentationValue(base, args, method, this.se);
		if(method.getName().equals(Constants.GET_MESSAGE_BODY) || method.getName().equals(Constants.GET_DISPLAY_MESSAGE_BODY)) {
			scvt = new StringConstantValueTag(Constants.SMS_BODY_TAG);
		}else if(method.getName().equals(Constants.GET_ORIGINATING_ADDRESS) || method.getName().equals(Constants.GET_DISPLAY_ORIGINATING_ADDRESS)) {
			scvt = new StringConstantValueTag(Constants.SMS_SENDER_TAG);
		}
		if(scvt != null) {
			mrv.addTag(scvt);
			results.add(mrv);
		}
		return results;
	}
}
