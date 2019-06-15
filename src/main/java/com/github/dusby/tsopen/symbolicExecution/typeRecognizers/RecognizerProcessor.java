package com.github.dusby.tsopen.symbolicExecution.typeRecognizers;

import org.javatuples.Pair;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecutioner;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValueProvider;

import soot.Value;
import soot.jimple.DefinitionStmt;

public abstract class RecognizerProcessor implements RecognizerProvider {
	
	private RecognizerProcessor next;
	protected SymbolicExecutioner se;

	public RecognizerProcessor(RecognizerProcessor next, SymbolicExecutioner se) {
		this.next = next;
		this.se = se;
	}

	@Override
	public Pair<Value, SymbolicValueProvider> recognize(DefinitionStmt def) {
		
		Pair<Value, SymbolicValueProvider> result = this.processRecognition(def);
		
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