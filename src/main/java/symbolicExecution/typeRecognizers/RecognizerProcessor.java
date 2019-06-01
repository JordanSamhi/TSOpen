package symbolicExecution.typeRecognizers;

import java.util.Map;

import soot.Value;
import soot.jimple.DefinitionStmt;
import symbolicExecution.SymbolicExecutioner;
import symbolicExecution.symbolicValues.SymbolicValueProvider;

public abstract class RecognizerProcessor implements RecognizerProvider {
	
	private RecognizerProcessor next;
	protected SymbolicExecutioner se;

	public RecognizerProcessor(RecognizerProcessor next, SymbolicExecutioner se) {
		this.next = next;
		this.se = se;
	}

	@Override
	public Map<Value, SymbolicValueProvider> recognize(DefinitionStmt def) {
		
		Map<Value, SymbolicValueProvider> result = this.processRecognition(def);
		
		if(result != null) {
			return result;
		}
		if(this.next != null) {
			return this.next.processRecognition(def);
		}
		else {
			return null;
		}
	}
}