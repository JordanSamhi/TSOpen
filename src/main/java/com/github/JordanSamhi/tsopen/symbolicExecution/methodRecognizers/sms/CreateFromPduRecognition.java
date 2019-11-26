package com.github.JordanSamhi.tsopen.symbolicExecution.methodRecognizers.sms;

import com.github.JordanSamhi.tsopen.symbolicExecution.SymbolicExecution;
import com.github.JordanSamhi.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import com.github.JordanSamhi.tsopen.utils.Constants;

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
