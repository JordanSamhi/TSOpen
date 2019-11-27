package lu.uni.tsopen.symbolicExecution.methodRecognizers.strings;

import java.util.ArrayList;
import java.util.List;

import lu.uni.tsopen.symbolicExecution.SymbolicExecution;
import lu.uni.tsopen.symbolicExecution.symbolicValues.MethodRepresentationValue;
import lu.uni.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import lu.uni.tsopen.utils.Constants;
import soot.SootMethod;
import soot.Value;
import soot.tagkit.StringConstantValueTag;

public class GetMessageBodyRecognition extends StringMethodsRecognitionHandler {

	public GetMessageBodyRecognition(StringMethodsRecognitionHandler next, SymbolicExecution se) {
		super(next, se);
	}

	@Override
	public List<SymbolicValue> processStringMethod(SootMethod method, Value base, List<Value> args) {
		List<SymbolicValue> results = new ArrayList<SymbolicValue>();
		MethodRepresentationValue mrv = new MethodRepresentationValue(base, args, method, this.se);
		if(method.getName().equals(Constants.GET_MESSAGE_BODY) || method.getName().equals(Constants.GET_DISPLAY_MESSAGE_BODY)) {
			mrv.addTag(new StringConstantValueTag(Constants.SMS_BODY_TAG));
			this.addResult(results, mrv);
		}
		return results;
	}
}
