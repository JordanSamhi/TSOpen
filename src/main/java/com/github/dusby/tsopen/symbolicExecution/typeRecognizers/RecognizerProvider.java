package com.github.dusby.tsopen.symbolicExecution.typeRecognizers;

import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValueProvider;

import soot.Value;

public interface RecognizerProvider {
	public SymbolicValueProvider recognize(Value leftOp, Value rightOp);
	public SymbolicValueProvider processRecognition(Value leftOp, Value rightOp);

}