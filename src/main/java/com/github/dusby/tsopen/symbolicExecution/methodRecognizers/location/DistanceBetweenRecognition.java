package com.github.dusby.tsopen.symbolicExecution.methodRecognizers.location;

import java.util.List;

import com.github.dusby.tsopen.symbolicExecution.ContextualValues;
import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.MethodRepresentationValue;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import com.github.dusby.tsopen.utils.Constants;
import com.github.dusby.tsopen.utils.Utils;

import soot.SootMethod;
import soot.Value;
import soot.tagkit.StringConstantValueTag;

public class DistanceBetweenRecognition extends LocationMethodsRecognitionHandler {

	public DistanceBetweenRecognition(LocationMethodsRecognitionHandler next, SymbolicExecution se) {
		super(next, se);
	}

	@Override
	public boolean processLocationMethod(SootMethod method, SymbolicValue sv) {
		MethodRepresentationValue mrv = null;
		Value lastArg = null;
		List<Value> args = null;
		ContextualValues contextualValues = null;
		List<SymbolicValue> values = null;

		if(method.getName().equals(Constants.DISTANCE_BETWEEN)) {
			if(sv instanceof MethodRepresentationValue) {
				mrv = (MethodRepresentationValue) sv;
				args = mrv.getArgs();
				for(Value arg : args) {
					if(Utils.containsTag(arg, Constants.LATITUDE_TAG, this.se)
							|| Utils.containsTag(arg, Constants.LONGITUDE_TAG, this.se)) {
						lastArg = args.get(args.size() - 1);
						contextualValues = this.se.getContextualValues(lastArg);
						if(contextualValues != null) {
							values = contextualValues.getLastCoherentValues(null);
							if(values != null) {
								for(SymbolicValue symval : values) {
									symval.addTag(new StringConstantValueTag(Constants.SUSPICIOUS));
								}
							}
						}
					}
				}
			}
			return true;
		}
		return false;
	}

}
