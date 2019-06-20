package com.github.dusby.tsopen.symbolicExecution.typeRecognizers;

import java.util.LinkedList;
import java.util.List;

import org.javatuples.Pair;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.ObjectValue;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;

import soot.SootMethod;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;
import soot.tagkit.StringConstantValueTag;

public class DateRecognizer extends TypeRecognizerHandler {

	private static final String NOW = "#now";

	public DateRecognizer(TypeRecognizerHandler next, SymbolicExecution se, InfoflowCFG icfg) {
		super(next, se, icfg);
		this.authorizedTypes.add("java.util.Date");
	}

	@Override
	public List<Pair<Value, SymbolicValue>> processRecognitionOfDefStmt(DefinitionStmt defUnit) {
		return null;
	}

	@Override
	public List<Pair<Value, SymbolicValue>> processRecognitionOfInvokeStmt(InvokeStmt invUnit) {
		Value base = null;
		InvokeExpr invExprUnit = null;
		SootMethod m = null;
		List<Value> args = null;
		List<Pair<Value, SymbolicValue>> results = new LinkedList<Pair<Value,SymbolicValue>>();
		ObjectValue date = null;

		invExprUnit = invUnit.getInvokeExpr();
		if(invExprUnit instanceof SpecialInvokeExpr) {
			m = invExprUnit.getMethod();
			if(m.isConstructor()) {
				base = ((SpecialInvokeExpr) invExprUnit).getBase();
				if(this.isAuthorizedType(base.getType().toString())) {
					args = invExprUnit.getArgs();
					date = new ObjectValue(base.getType(), args, this.se);
					if(args.size() == 0) {
						date.addTag(new StringConstantValueTag(NOW));
					}
					results.add(new Pair<Value, SymbolicValue>(base, date));
				}
			}
		}
		return results;
	}
}
