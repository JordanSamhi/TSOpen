package lu.uni.tsopen.symbolicExecution.methodRecognizers.dateTime;

import lu.uni.tsopen.symbolicExecution.SymbolicExecution;
import lu.uni.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import lu.uni.tsopen.utils.Constants;
import soot.SootMethod;
import soot.tagkit.StringConstantValueTag;

public class SetToNowRecognition extends DateTimeMethodsRecognitionHandler {

	public SetToNowRecognition(DateTimeMethodsRecognitionHandler next, SymbolicExecution se) {
		super(next, se);
	}

	@Override
	public boolean processDateTimeMethod(SootMethod method, SymbolicValue sv) {
		String className = method.getDeclaringClass().getName();
		String methodName = method.getName();
		if(methodName.equals(Constants.SET_TO_NOW) && (className.equals(Constants.ANDROID_TEXT_FORMAT_TIME))) {
			sv.addTag(new StringConstantValueTag(Constants.NOW_TAG));
			return true;
		}
		return false;
	}

}
