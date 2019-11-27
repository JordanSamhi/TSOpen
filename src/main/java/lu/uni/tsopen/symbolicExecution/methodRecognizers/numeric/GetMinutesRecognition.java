package lu.uni.tsopen.symbolicExecution.methodRecognizers.numeric;

import lu.uni.tsopen.symbolicExecution.SymbolicExecution;
import lu.uni.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import lu.uni.tsopen.utils.Constants;
import soot.SootMethod;
import soot.Value;

public class GetMinutesRecognition extends NumericMethodsRecognitionHandler {

	public GetMinutesRecognition(NumericMethodsRecognitionHandler next, SymbolicExecution se) {
		super(next, se);
	}

	@Override
	public boolean processNumericMethod(SootMethod method, Value base, SymbolicValue sv) {
		return this.genericProcessNumericMethod(method, base, sv, Constants.JAVA_UTIL_DATE, Constants.GET_MINUTES, Constants.NOW_TAG, Constants.MINUTES_TAG);
	}

}
