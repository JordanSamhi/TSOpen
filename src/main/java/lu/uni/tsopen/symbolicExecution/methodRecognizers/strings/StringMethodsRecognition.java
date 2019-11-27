package lu.uni.tsopen.symbolicExecution.methodRecognizers.strings;

import java.util.List;

import lu.uni.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import soot.SootMethod;
import soot.Value;

public interface StringMethodsRecognition {
	public List<SymbolicValue> recognizeStringMethod(SootMethod method, Value base, List<Value> args);
	public List<SymbolicValue> processStringMethod(SootMethod method, Value base, List<Value> args);
}
