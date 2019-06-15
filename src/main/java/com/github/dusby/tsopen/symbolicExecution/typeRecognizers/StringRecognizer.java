package com.github.dusby.tsopen.symbolicExecution.typeRecognizers;

import java.util.List;

import org.javatuples.Pair;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecutioner;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.ConcreteValue;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValueProvider;

import soot.Local;
import soot.SootMethod;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.NewExpr;
import soot.jimple.StringConstant;

public class StringRecognizer extends RecognizerProcessor{

	public StringRecognizer(RecognizerProcessor next, SymbolicExecutioner se) {
		super(next, se);
	}

	@Override
	public Pair<Value, SymbolicValueProvider> processRecognition(DefinitionStmt def) {
		Value leftOp = def.getLeftOp();
		String leftOpType = leftOp.getType().toQuotedString(); 
		if(leftOpType.equals("java.lang.String")
				|| leftOpType.equals("java.lang.StringBuilder")
				|| leftOpType.equals("java.lang.StringBuffer")) {
			Pair<Value, SymbolicValueProvider> result = null;
			Value rightOp = def.getRightOp();

			if(rightOp instanceof StringConstant) {
				result = new Pair<Value, SymbolicValueProvider>(leftOp, new ConcreteValue((StringConstant)rightOp));
			}else if(rightOp instanceof Local) {
				result = new Pair<Value, SymbolicValueProvider>(leftOp, this.se.getModelContext().get(rightOp));
			}
			// TODO retrieve parameter value
			if(rightOp instanceof NewExpr) {
				// TODO retrieve string in constructor
				result = new Pair<Value, SymbolicValueProvider>(leftOp, new ConcreteValue(StringConstant.v("")));
			}

			if(rightOp instanceof InvokeExpr) {
				InvokeExpr rightOpInvokeExpr = (InvokeExpr) rightOp;
				SootMethod m = rightOpInvokeExpr.getMethod();
				List<Value> args = rightOpInvokeExpr.getArgs();
				Value base = rightOpInvokeExpr instanceof InstanceInvokeExpr ? ((InstanceInvokeExpr) rightOpInvokeExpr).getBase() : null;
				result = new Pair<Value, SymbolicValueProvider>(leftOp, new SymbolicValue(base, args, m, this.se));
			}
			return result;
		}
		return null;
	}
}
