package lu.uni.tsopen.symbolicExecution.methodRecognizers.dateTime;

import lu.uni.tsopen.symbolicExecution.SymbolicExecution;
import lu.uni.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import lu.uni.tsopen.utils.Constants;
import soot.SootMethod;
import soot.tagkit.StringConstantValueTag;

public class GetInstanceRecognition extends DateTimeMethodsRecognitionHandler {

	public GetInstanceRecognition(DateTimeMethodsRecognitionHandler next, SymbolicExecution se) {
		super(next, se);
	}

	@Override
	public boolean processDateTimeMethod(SootMethod method, SymbolicValue sv) {
		String className = method.getDeclaringClass().getName();
		String methodName = method.getName();
		if((methodName.equals(Constants.GET_INSTANCE) && (className.equals(Constants.JAVA_UTIL_CALENDAR) || className.equals(Constants.JAVA_UTIL_GREGORIAN_CALENDAR)))) {
			sv.addTag(new StringConstantValueTag(Constants.NOW_TAG));
			return true;
		}
		return false;
	}

}
