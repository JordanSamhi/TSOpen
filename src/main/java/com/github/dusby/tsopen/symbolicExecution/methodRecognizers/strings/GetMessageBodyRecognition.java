package com.github.dusby.tsopen.symbolicExecution.methodRecognizers.strings;

import java.util.ArrayList;
import java.util.List;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.MethodRepresentationValue;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;

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
		MethodRepresentationValue mrv = new MethodRepresentationValue(base, args, method, this.se);
		if(method.getName().equals(GET_MESSAGE_BODY) || method.getName().equals(GET_DISPLAY_MESAGE_BODY)) {
			mrv.addTag(new StringConstantValueTag(BODY_TAG));
		}else if(method.getName().equals(GET_ORIGINATING_ADDRESS) || method.getName().equals(GET_DISPLAY_ORIGINATING_ADDRESS)) {
			mrv.addTag(new StringConstantValueTag(SENDER_TAG));
		}
		results.add(mrv);
		return results;
	}
}