package lu.uni.tsopen.symbolicExecution.methodRecognizers.numeric;

import lu.uni.tsopen.symbolicExecution.SymbolicExecution;
import lu.uni.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import lu.uni.tsopen.utils.Constants;
import soot.SootMethod;
import soot.Value;
import soot.tagkit.StringConstantValueTag;

public class GetRecognition extends NumericMethodsRecognitionHandler {

	public GetRecognition(NumericMethodsRecognitionHandler next, SymbolicExecution se) {
		super(next, se);
	}

	@Override
	public boolean processNumericMethod(SootMethod method, Value base, SymbolicValue sv) {
		String className = method.getDeclaringClass().getName();
		String methodName = method.getName();
		if((methodName.equals(Constants.GET) && (className.equals(Constants.JAVA_UTIL_CALENDAR) || className.equals(Constants.JAVA_UTIL_GREGORIAN_CALENDAR)))) {
			sv.addTag(new StringConstantValueTag(Constants.NOW_TAG));
			return true;
		}
		return false;
	}

}
