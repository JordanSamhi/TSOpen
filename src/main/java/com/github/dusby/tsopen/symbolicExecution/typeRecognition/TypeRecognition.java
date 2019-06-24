package com.github.dusby.tsopen.symbolicExecution.typeRecognition;

import java.util.List;

import org.javatuples.Pair;

import com.github.dusby.tsopen.symbolicExecution.symbolicValues.ObjectValue;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;

import soot.Unit;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;

public interface TypeRecognition {
	public List<Pair<Value, SymbolicValue>> recognizeType(Unit node);
	public List<Pair<Value, SymbolicValue>> processDefinitionStmt(DefinitionStmt defUnit);
	public List<Pair<Value, SymbolicValue>> processInvokeStmt(InvokeStmt invUnit);
	public void handleConstructor(InvokeExpr invExprUnit, Value base, List<Pair<Value, SymbolicValue>> results);
	public abstract void handleConstructorTag(List<Value> args, ObjectValue object);
	public List<Pair<Value, SymbolicValue>> handleDefinitionStmt(DefinitionStmt defUnit);
	public boolean containsTag(Value base, String nowTag);
}