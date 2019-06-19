package com.github.dusby.tsopen.symbolicExecution.typeRecognizers;

import java.util.List;

import org.javatuples.Pair;

import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValueProvider;

import soot.Unit;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.InvokeStmt;

public interface TypeRecognizerProvider {
	public List<Pair<Value, SymbolicValueProvider>> recognize(Unit node);
	public List<Pair<Value, SymbolicValueProvider>> processRecognitionOfDefStmt(DefinitionStmt defUnit);
	public List<Pair<Value, SymbolicValueProvider>> processRecognitionOfInvokeStmt(InvokeStmt invUnit);
}