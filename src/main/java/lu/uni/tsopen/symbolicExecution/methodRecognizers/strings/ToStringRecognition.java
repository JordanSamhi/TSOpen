package lu.uni.tsopen.symbolicExecution.methodRecognizers.strings;

import java.util.ArrayList;
import java.util.List;

import lu.uni.tsopen.symbolicExecution.SymbolicExecution;
import lu.uni.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import lu.uni.tsopen.utils.Constants;
import lu.uni.tsopen.utils.Utils;
import soot.SootMethod;
import soot.Value;

public class ToStringRecognition extends StringMethodsRecognitionHandler {

	public ToStringRecognition(StringMethodsRecognitionHandler next, SymbolicExecution se) {
		super(next, se);
	}

	@Override
	public List<SymbolicValue> processStringMethod(SootMethod method, Value base, List<Value> args) {
		List<SymbolicValue> results = new ArrayList<SymbolicValue>();
		if(method.getName().equals(Constants.TOSTRING)) {
			this.addSimpleResult(base, results);
			for(SymbolicValue sv : results) {
				Utils.propagateTags(base, sv, this.se);
			}
			return results;
		}
		return null;
	}

}
