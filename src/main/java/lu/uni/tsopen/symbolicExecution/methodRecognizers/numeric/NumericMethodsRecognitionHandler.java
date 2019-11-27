package lu.uni.tsopen.symbolicExecution.methodRecognizers.numeric;

import lu.uni.tsopen.symbolicExecution.SymbolicExecution;
import lu.uni.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import lu.uni.tsopen.utils.Utils;
import soot.SootMethod;
import soot.Value;
import soot.tagkit.StringConstantValueTag;

public abstract class NumericMethodsRecognitionHandler implements NumericMethodsRecognition {

	private NumericMethodsRecognitionHandler next;
	protected SymbolicExecution se;

	public NumericMethodsRecognitionHandler(NumericMethodsRecognitionHandler next, SymbolicExecution se) {
		this.next = next;
		this.se = se;
	}

	@Override
	public boolean recognizeNumericMethod(SootMethod method, Value base, SymbolicValue sv) {
		boolean recognized = this.processNumericMethod(method, base, sv);

		if(recognized) {
			return recognized;
		}
		if(this.next != null) {
			return this.next.recognizeNumericMethod(method, base, sv);
		}
		else {
			return false;
		}
	}

	@Override
	public boolean genericProcessNumericMethod(SootMethod method, Value base, SymbolicValue sv,
			String className, String methodName, String containedTag, String addedTag) {
		if(method.getDeclaringClass().getName().equals(className) && method.getName().equals(methodName)) {
			if(this.isTagHandled(containedTag, addedTag, base, sv)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isTagHandled(String containedTag, String addedTag, Value base, SymbolicValue sv) {
		if(Utils.containsTag(base, containedTag, this.se)) {
			sv.addTag(new StringConstantValueTag(addedTag));
			return true;
		}
		return false;
	}
}
