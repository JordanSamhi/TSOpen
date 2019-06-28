package com.github.dusby.tsopen.symbolicExecution.methodRecognizers.numeric;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import com.github.dusby.tsopen.utils.Constants;

import soot.SootMethod;
import soot.Value;

public class GetYearRecognition extends NumericMethodsRecognitionHandler {

	public GetYearRecognition(NumericMethodsRecognitionHandler next, SymbolicExecution se) {
		super(next, se);
	}

	@Override
	public boolean processNumericMethod(SootMethod method, Value base, SymbolicValue sv) {
		return this.genericProcessNumericMethod(method, base, sv, Constants.JAVA_UTIL_DATE, Constants.GET_YEAR, Constants.NOW_TAG, Constants.YEAR_TAG);
	}

}
