package symbolicExecution.typeRecognizers;

import java.util.Map;

import soot.Value;
import soot.jimple.DefinitionStmt;
import symbolicExecution.symbolicValues.SymbolicValueProvider;

public interface RecognizerProvider {
	public Map<Value, SymbolicValueProvider> recognize(DefinitionStmt def);
	public Map<Value, SymbolicValueProvider> processRecognition(DefinitionStmt def);

}