package lu.uni.tsopen.symbolicExecution.methodRecognizers.bool;

import java.util.List;

import lu.uni.tsopen.symbolicExecution.SymbolicExecution;
import lu.uni.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import lu.uni.tsopen.utils.Constants;
import lu.uni.tsopen.utils.Utils;
import soot.SootMethod;
import soot.Value;
import soot.jimple.Constant;
import soot.tagkit.StringConstantValueTag;

public class ContainsRecognition extends BooleanMethodsRecognitionHandler {

	public ContainsRecognition(BooleanMethodsRecognitionHandler next, SymbolicExecution se) {
		super(next, se);
	}

	@Override
	public boolean processBooleanMethod(SootMethod method, Value base, SymbolicValue sv, List<Value> args) {
		Value firstArg = null;
		String methodName = method.getName();
		if(methodName.equals(Constants.CONTAINS)) {
			firstArg = args.get(0);
			if(Utils.containsTag(base, Constants.SMS_BODY_TAG, this.se) || Utils.containsTag(base, Constants.SMS_SENDER_TAG, this.se)) {
				if(firstArg instanceof Constant) {
					sv.addTag(new StringConstantValueTag(Constants.SUSPICIOUS));
					return true;
				}
			}
		}
		return false;
	}

}
