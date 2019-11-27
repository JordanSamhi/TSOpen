package lu.uni.tsopen.symbolicExecution.methodRecognizers.bool;

import java.util.List;

import lu.uni.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import soot.SootMethod;
import soot.Value;

public interface BooleanMethodsRecognition {
	public boolean recognizeBooleanMethod(SootMethod method, Value base, SymbolicValue sv, List<Value> args);
	public boolean processBooleanMethod(SootMethod method, Value base, SymbolicValue sv, List<Value> args);
}
