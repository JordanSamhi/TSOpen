package lu.uni.tsopen.symbolicExecution.methodRecognizers.location;

import lu.uni.tsopen.symbolicExecution.SymbolicExecution;
import lu.uni.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import lu.uni.tsopen.utils.Constants;
import soot.SootMethod;
import soot.tagkit.StringConstantValueTag;

public class GetLastKnowLocationRecognition extends LocationMethodsRecognitionHandler {

	public GetLastKnowLocationRecognition(LocationMethodsRecognitionHandler next, SymbolicExecution se) {
		super(next, se);
	}

	@Override
	public boolean processLocationMethod(SootMethod method, SymbolicValue sv) {
		String className = method.getDeclaringClass().getName();
		String methodName = method.getName();
		if((className.equals(Constants.ANDROID_LOCATION_LOCATION_MANAGER) && methodName.equals(Constants.GET_LAST_KNOW_LOCATION))) {
			sv.addTag(new StringConstantValueTag(Constants.HERE_TAG));
			return true;
		}
		return false;
	}

}