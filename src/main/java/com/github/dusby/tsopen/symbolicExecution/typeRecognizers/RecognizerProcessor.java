package com.github.dusby.tsopen.symbolicExecution.typeRecognizers;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecutioner;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValueProvider;

import soot.Unit;
import soot.jimple.DefinitionStmt;

public abstract class RecognizerProcessor implements RecognizerProvider {
	
	private RecognizerProcessor next;
	protected SymbolicExecutioner se;

	public RecognizerProcessor(RecognizerProcessor next, SymbolicExecutioner se) {
		this.next = next;
		this.se = se;
	}

	@Override
	public SymbolicValueProvider recognize(DefinitionStmt defUnit, Unit node) {
		
		SymbolicValueProvider result = this.processRecognition(defUnit, node);
		
		if(result != null) {
			return result;
		}
		if(this.next != null) {
			return this.next.processRecognition(defUnit, node);
		}
		else {
			return null;
		}
	}
}