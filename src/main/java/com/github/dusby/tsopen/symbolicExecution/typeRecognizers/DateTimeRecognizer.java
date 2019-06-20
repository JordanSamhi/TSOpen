package com.github.dusby.tsopen.symbolicExecution.typeRecognizers;

import java.util.LinkedList;
import java.util.List;

import org.javatuples.Pair;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.ObjectValue;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;

import soot.SootClass;
import soot.SootMethod;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;
import soot.tagkit.StringConstantValueTag;

public class DateTimeRecognizer extends TypeRecognizerHandler {

	public DateTimeRecognizer(TypeRecognizerHandler next, SymbolicExecution se, InfoflowCFG icfg) {
		super(next, se, icfg);
		this.authorizedTypes.add(JAVA_TIME_LOCAL_DATE);
		this.authorizedTypes.add(JAVA_UTIL_CALENDAR);
		this.authorizedTypes.add(JAVA_UTIL_GREGORIAN_CALENDAR);
		this.authorizedTypes.add(JAVA_TIME_LOCAL_DATE_TIME);
		this.authorizedTypes.add(JAVA_TIME_LOCAL_DATE);
	}

	@Override
	public List<Pair<Value, SymbolicValue>> processRecognitionOfDefStmt(DefinitionStmt defUnit) {
		Value leftOp = defUnit.getLeftOp(),
				rightOp = defUnit.getRightOp();
		String leftOpType = leftOp.getType().toString(),
				methodName = null;
		List<Pair<Value, SymbolicValue>> results = new LinkedList<Pair<Value,SymbolicValue>>();
		StaticInvokeExpr rightOpStaticInvokeExpr = null;
		SootMethod method = null;
		SootClass sootClass = null;
		List<Value> args = null;
		ObjectValue date = null;

		if(this.isAuthorizedType(leftOpType)) {
			if(rightOp instanceof StaticInvokeExpr) {
				rightOpStaticInvokeExpr = (StaticInvokeExpr) rightOp;
				method = rightOpStaticInvokeExpr.getMethod();
				sootClass = method.getDeclaringClass();
				methodName = method.getName();
				if(methodName.equals(GET_INSTANCE_METHOD)
						|| methodName.equals(NOW_METHOD)) {
					args = rightOpStaticInvokeExpr.getArgs();
					date = new ObjectValue(sootClass.getType(), args, this.se);
					date.addTag(new StringConstantValueTag(NOW_TAG));
					results.add(new Pair<Value, SymbolicValue>(leftOp, date));
				}
			}
		}
		return results;
	}

	@Override
	public List<Pair<Value, SymbolicValue>> processRecognitionOfInvokeStmt(InvokeStmt invUnit) {

		Value base = null;
		InvokeExpr invExprUnit = invUnit.getInvokeExpr();
		SootMethod m = null;
		List<Value> args = null;
		List<Pair<Value, SymbolicValue>> results = new LinkedList<Pair<Value,SymbolicValue>>();
		ObjectValue date = null;

		if(invExprUnit instanceof SpecialInvokeExpr) {
			m = invExprUnit.getMethod();
			if(m.isConstructor()) {
				base = ((SpecialInvokeExpr) invExprUnit).getBase();
				if(this.isAuthorizedType(base.getType().toString())) {
					args = invExprUnit.getArgs();
					date = new ObjectValue(base.getType(), args, this.se);
					if(args.size() == 0) {
						date.addTag(new StringConstantValueTag(NOW_TAG));
					}
					results.add(new Pair<Value, SymbolicValue>(base, date));
				}
			}
		}
		return results;
	}
}
