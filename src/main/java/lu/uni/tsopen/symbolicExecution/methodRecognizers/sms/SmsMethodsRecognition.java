package lu.uni.tsopen.symbolicExecution.methodRecognizers.sms;

import lu.uni.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import soot.SootMethod;

public interface SmsMethodsRecognition {
	public boolean recognizeSmsMethod(SootMethod method, SymbolicValue sv);
	public boolean processSmsMethod(SootMethod method, SymbolicValue sv);
}
