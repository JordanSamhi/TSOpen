package com.github.dusby.tsopen.symbolicExecution.typeRecognizers;

import java.util.List;

import org.javatuples.Pair;

import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValueProvider;

import soot.Unit;
import soot.Value;

public interface RecognizerProvider {
	public List<Pair<Value, SymbolicValueProvider>> recognize(Unit node);
	public List<Pair<Value, SymbolicValueProvider>> processRecognition(Unit node);

}