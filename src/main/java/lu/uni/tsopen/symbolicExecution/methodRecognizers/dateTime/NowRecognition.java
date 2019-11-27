package lu.uni.tsopen.symbolicExecution.methodRecognizers.dateTime;

import lu.uni.tsopen.symbolicExecution.SymbolicExecution;
import lu.uni.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import lu.uni.tsopen.utils.Constants;
import soot.SootMethod;
import soot.tagkit.StringConstantValueTag;

public class NowRecognition extends DateTimeMethodsRecognitionHandler {

	public NowRecognition(DateTimeMethodsRecognitionHandler next, SymbolicExecution se) {
		super(next, se);
	}

	@Override
	public boolean processDateTimeMethod(SootMethod method, SymbolicValue sv) {
		String className = method.getDeclaringClass().getName();
		String methodName = method.getName();
		if(methodName.equals(Constants.NOW) && (className.equals(Constants.JAVA_TIME_LOCAL_DATE_TIME) || className.equals(Constants.JAVA_TIME_LOCAL_DATE))) {
			sv.addTag(new StringConstantValueTag(Constants.NOW_TAG));
			return true;
		}
		return false;
	}

}