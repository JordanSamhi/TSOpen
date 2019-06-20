package com.github.dusby.tsopen.symbolicExecution.typeRecognizers;

import java.util.List;

import org.javatuples.Pair;

import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;

import soot.Unit;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.InvokeStmt;

public interface TypeRecognizerProvider {
	public List<Pair<Value, SymbolicValue>> recognize(Unit node);
	public List<Pair<Value, SymbolicValue>> processRecognitionOfDefStmt(DefinitionStmt defUnit);
	public List<Pair<Value, SymbolicValue>> processRecognitionOfInvokeStmt(InvokeStmt invUnit);
}