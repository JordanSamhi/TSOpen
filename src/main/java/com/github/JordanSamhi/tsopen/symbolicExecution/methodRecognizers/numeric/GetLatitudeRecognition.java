package com.github.JordanSamhi.tsopen.symbolicExecution.methodRecognizers.numeric;

import com.github.JordanSamhi.tsopen.symbolicExecution.SymbolicExecution;
import com.github.JordanSamhi.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import com.github.JordanSamhi.tsopen.utils.Constants;

import soot.SootMethod;
import soot.Value;

public class GetLatitudeRecognition extends NumericMethodsRecognitionHandler {

	public GetLatitudeRecognition(NumericMethodsRecognitionHandler next, SymbolicExecution se) {
		super(next, se);
	}

	@Override
	public boolean processNumericMethod(SootMethod method, Value base, SymbolicValue sv) {
		return this.genericProcessNumericMethod(method, base, sv, Constants.ANDROID_LOCATION_LOCATION, Constants.GET_LATITUDE, Constants.HERE_TAG, Constants.LATITUDE_TAG);
	}

}
