package com.github.JordanSamhi.tsopen.symbolicExecution.typeRecognition;

import java.util.List;

import org.javatuples.Pair;

import com.github.JordanSamhi.tsopen.symbolicExecution.symbolicValues.ObjectValue;
import com.github.JordanSamhi.tsopen.symbolicExecution.symbolicValues.SymbolicValue;

import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.ReturnStmt;

public interface TypeRecognition {
	public List<Pair<Value, SymbolicValue>> recognizeType(Unit node);
	public List<Pair<Value, SymbolicValue>> processDefinitionStmt(DefinitionStmt defUnit);
	public List<Pair<Value, SymbolicValue>> processInvokeStmt(InvokeStmt invUnit);
	public void handleConstructor(InvokeExpr invExprUnit, Value base, List<Pair<Value, SymbolicValue>> results);
	public void handleConstructorTag(List<Value> args, ObjectValue object);
	public List<Pair<Value, SymbolicValue>> handleDefinitionStmt(DefinitionStmt defUnit);
	public List<Pair<Value, SymbolicValue>> processReturnStmt(ReturnStmt node);
	public abstract void handleInvokeTag(List<Value> args, Value base, SymbolicValue object, SootMethod method);
}