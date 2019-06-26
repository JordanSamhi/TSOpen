package com.github.dusby.tsopen.symbolicExecution.methodRecognizers.strings;

import java.util.ArrayList;
import java.util.List;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import com.github.dusby.tsopen.utils.Constants;
import com.github.dusby.tsopen.utils.Utils;

import soot.SootMethod;
import soot.Value;

public class FormatRecognition extends StringMethodsRecognitionHandler {

	public FormatRecognition(StringMethodsRecognitionHandler next, SymbolicExecution se) {
		super(next, se);
	}

	@Override
	public List<SymbolicValue> processStringMethod(SootMethod method, Value base, List<Value> args) {
		List<SymbolicValue> results = new ArrayList<SymbolicValue>();
		Value effectiveArg = null;
		if(method.getName().equals(Constants.FORMAT) && base.getType().toString().equals(Constants.JAVA_TEXT_SIMPLE_DATE_FORMAT)) {
			this.addSimpleResult(base, results);
			if(args.size() > 0) {
				effectiveArg = args.get(0);
				for(SymbolicValue sv : results) {
					Utils.propagateTags(effectiveArg, sv, this.se);
				}
			}
			return results;
		}
		return null;
	}

}
