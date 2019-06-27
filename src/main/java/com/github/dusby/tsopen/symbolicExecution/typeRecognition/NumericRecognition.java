package com.github.dusby.tsopen.symbolicExecution.typeRecognition;

import java.util.LinkedList;
import java.util.List;

import org.javatuples.Pair;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;
import com.github.dusby.tsopen.symbolicExecution.methodRecognizers.numeric.NumericMethodsRecognitionHandler;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.FieldValue;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.MethodRepresentationValue;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.ObjectValue;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import com.github.dusby.tsopen.utils.Utils;

import soot.SootMethod;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;

public abstract class NumericRecognition extends TypeRecognitionHandler {

	protected NumericMethodsRecognitionHandler nmrh;

	public NumericRecognition(TypeRecognitionHandler next, SymbolicExecution se, InfoflowCFG icfg) {
		super(next, se, icfg);
	}

	@Override
	public void handleConstructorTag(List<Value> args, ObjectValue object) {}

	@Override
	public List<Pair<Value, SymbolicValue>> handleDefinitionStmt(DefinitionStmt defUnit) {
		Value leftOp = defUnit.getLeftOp(),
				rightOp = defUnit.getRightOp(),
				base = null;
		List<Pair<Value, SymbolicValue>> results = new LinkedList<Pair<Value,SymbolicValue>>();
		InstanceInvokeExpr rightOpInvExpr = null;
		StaticInvokeExpr rightOpStExpr = null;
		SootMethod method = null;
		List<Value> args = null;
		SymbolicValue object = null;
		InstanceFieldRef field = null;

		//TODO factorize
		if(rightOp instanceof InstanceInvokeExpr) {
			rightOpInvExpr = (InstanceInvokeExpr) rightOp;
			method = rightOpInvExpr.getMethod();
			args = rightOpInvExpr.getArgs();
			base = rightOpInvExpr.getBase();
			object = new MethodRepresentationValue(base, args, method, this.se);
			this.nmrh.recognizeNumericMethod(method, base, object);
		}else if (rightOp instanceof StaticInvokeExpr){
			rightOpStExpr = (StaticInvokeExpr) rightOp;
			method = rightOpStExpr.getMethod();
			args = rightOpStExpr.getArgs();
			object = new MethodRepresentationValue(base, args, method, this.se);
			this.nmrh.recognizeNumericMethod(method, base, object);
		}else if(rightOp instanceof InstanceFieldRef) {
			field = (InstanceFieldRef) rightOp;
			base = field.getBase();
			object = new FieldValue(base, field.getField().getName(), this.se);
			Utils.propagateTags(base, object, this.se);
		}else {
			return results;
		}

		results.add(new Pair<Value, SymbolicValue>(leftOp, object));
		return results;
	}

	@Override
	protected void handleInvokeTag(List<Value> args, Value base, SymbolicValue object, SootMethod method) {}
}
