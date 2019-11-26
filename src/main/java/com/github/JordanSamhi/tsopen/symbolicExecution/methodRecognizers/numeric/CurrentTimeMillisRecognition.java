package com.github.JordanSamhi.tsopen.symbolicExecution.methodRecognizers.numeric;

import com.github.JordanSamhi.tsopen.symbolicExecution.SymbolicExecution;
import com.github.JordanSamhi.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import com.github.JordanSamhi.tsopen.utils.Constants;

import soot.SootMethod;
import soot.Value;
import soot.tagkit.StringConstantValueTag;

public class CurrentTimeMillisRecognition extends NumericMethodsRecognitionHandler {

	public CurrentTimeMillisRecognition(NumericMethodsRecognitionHandler next, SymbolicExecution se) {
		super(next, se);
	}

	@Override
	public boolean processNumericMethod(SootMethod method, Value base, SymbolicValue sv) {
		return this.genericProcessNumericMethod(method, base, sv, Constants.JAVA_LANG_SYSTEM, Constants.CURRENT_TIME_MILLIS, null, Constants.NOW_TAG);
	}

	@Override
	public boolean isTagHandled(String containedTag, String addedTag, Value base, SymbolicValue sv) {
		sv.addTag(new StringConstantValueTag(addedTag));
		return true;
	}

}
