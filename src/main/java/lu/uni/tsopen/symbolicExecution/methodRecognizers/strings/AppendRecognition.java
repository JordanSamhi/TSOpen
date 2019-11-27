package lu.uni.tsopen.symbolicExecution.methodRecognizers.strings;

import java.util.ArrayList;
import java.util.List;

import lu.uni.tsopen.symbolicExecution.ContextualValues;
import lu.uni.tsopen.symbolicExecution.SymbolicExecution;
import lu.uni.tsopen.symbolicExecution.symbolicValues.ConstantValue;
import lu.uni.tsopen.symbolicExecution.symbolicValues.MethodRepresentationValue;
import lu.uni.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import lu.uni.tsopen.symbolicExecution.symbolicValues.UnknownValue;
import lu.uni.tsopen.utils.Constants;
import lu.uni.tsopen.utils.Utils;
import soot.SootMethod;
import soot.Value;
import soot.jimple.Constant;
import soot.jimple.StringConstant;

public class AppendRecognition extends StringMethodsRecognitionHandler {

	public AppendRecognition(StringMethodsRecognitionHandler next, SymbolicExecution se) {
		super(next, se);
	}

	@Override
	public List<SymbolicValue> processStringMethod(SootMethod method, Value base, List<Value> args) {
		List<SymbolicValue> values = null;
		List<SymbolicValue> results = new ArrayList<SymbolicValue>();
		ContextualValues contextualValuesOfBase = null;
		if(method.getName().equals(Constants.APPEND)) {
			contextualValuesOfBase = this.se.getContext().get(base);
			if(contextualValuesOfBase == null) {
				results.addAll(this.computeValue(new UnknownValue(this.se), args, base, method));
			}else {
				values = contextualValuesOfBase.getLastCoherentValues(null);
				if(values != null) {
					for(SymbolicValue sv : values) {
						results.addAll(this.computeValue(sv, args, base, method));
					}
				}
			}
			for(SymbolicValue sv : results) {
				Utils.propagateTags(base, sv, this.se);
			}
			return results;
		}
		return null;
	}

	private List<SymbolicValue> computeValue(SymbolicValue symVal, List<Value> args, Value base, SootMethod method) {
		List<SymbolicValue> results = new ArrayList<SymbolicValue>();
		Value effectiveArg = args.get(0);
		ContextualValues contextualValuesOfBase = null;
		List<SymbolicValue> values = null;
		SymbolicValue object = null;
		if(effectiveArg instanceof Constant) {
			if(symVal.isConstant()) {
				object = new ConstantValue(StringConstant.v(String.format("%s%s", symVal, effectiveArg)), this.se);
			}else {
				object = new MethodRepresentationValue(base, args, method, this.se);
			}
			this.addResult(results, object);
		}else {
			contextualValuesOfBase = this.se.getContext().get(effectiveArg);
			if(contextualValuesOfBase == null) {
				if(symVal.isConstant()) {
					object = new ConstantValue(StringConstant.v(String.format("%s%s", symVal, Constants.UNKNOWN_STRING)), this.se);
				}else {
					object = new MethodRepresentationValue(base, args, method, this.se);
				}
				this.addResult(results, object);
			}else {
				values = contextualValuesOfBase.getLastCoherentValues(null);
				if(values != null) {
					for(SymbolicValue sv : values) {
						if(symVal.isConstant() && sv.isConstant()) {
							object = new ConstantValue(StringConstant.v(String.format("%s%s", symVal, sv)), this.se);
						}else {
							object = new MethodRepresentationValue(base, args, method, this.se);
						}
						this.addResult(results, object);
					}
				}
			}
		}
		for(SymbolicValue sv : results) {
			for(Value arg : args) {
				Utils.propagateTags(arg, sv, this.se);
			}
		}
		return results;
	}
}
