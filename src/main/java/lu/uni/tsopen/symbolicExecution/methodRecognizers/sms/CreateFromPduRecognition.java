package lu.uni.tsopen.symbolicExecution.methodRecognizers.sms;

import lu.uni.tsopen.symbolicExecution.SymbolicExecution;
import lu.uni.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import lu.uni.tsopen.utils.Constants;
import soot.SootClass;
import soot.SootMethod;
import soot.tagkit.StringConstantValueTag;

public class CreateFromPduRecognition extends SmsMethodsRecognitionHandler {

	public CreateFromPduRecognition(SmsMethodsRecognitionHandler next, SymbolicExecution se) {
		super(next, se);
	}

	@Override
	public boolean processSmsMethod(SootMethod method, SymbolicValue sv) {
		SootClass declaringClass = method.getDeclaringClass();
		String methodName = method.getName();
		if(methodName.equals(Constants.CREATE_FROM_PDU) && declaringClass.getName().equals(Constants.ANDROID_TELEPHONY_SMSMESSAGE)) {
			sv.addTag(new StringConstantValueTag(Constants.SMS_TAG));
			return true;
		}
		return false;
	}

}
