package lu.uni.tsopen.symbolicExecution.methodRecognizers.numeric;

import lu.uni.tsopen.symbolicExecution.SymbolicExecution;
import lu.uni.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import lu.uni.tsopen.utils.Constants;
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
