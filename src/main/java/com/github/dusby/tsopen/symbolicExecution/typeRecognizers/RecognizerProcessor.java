package com.github.dusby.tsopen.symbolicExecution.typeRecognizers;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecutioner;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValueProvider;

import soot.Value;

public abstract class RecognizerProcessor implements RecognizerProvider {
	
	private RecognizerProcessor next;
	protected SymbolicExecutioner se;

	public RecognizerProcessor(RecognizerProcessor next, SymbolicExecutioner se) {
		this.next = next;
		this.se = se;
	}

	@Override
	public SymbolicValueProvider recognize(Value leftOp, Value rightOp) {
		
		SymbolicValueProvider result = this.processRecognition(leftOp, rightOp);
		
		if(result != null) {
			return result;
		}
		if(this.next != null) {
			return this.next.processRecognition(leftOp, rightOp);
		}
		else {
			return null;
		}
	}
}