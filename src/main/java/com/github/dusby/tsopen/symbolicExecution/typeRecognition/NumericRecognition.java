package com.github.dusby.tsopen.symbolicExecution.typeRecognition;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.javatuples.Pair;

import com.github.dusby.tsopen.symbolicExecution.ContextualValues;
import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;
import com.github.dusby.tsopen.symbolicExecution.methodRecognizers.numeric.NumericMethodsRecognitionHandler;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.BinOpValue;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.FieldValue;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.MethodRepresentationValue;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.ObjectValue;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.UnknownValue;
import com.github.dusby.tsopen.utils.Utils;

import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.BinopExpr;
import soot.jimple.DefinitionStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.ParameterRef;
import soot.jimple.StaticFieldRef;
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
				base = null,
				binOp1 = null,
				binOp2 = null;
		List<Pair<Value, SymbolicValue>> results = new LinkedList<Pair<Value,SymbolicValue>>();
		InvokeExpr rightOpInvExpr = null;
		SootMethod method = null;
		List<Value> args = null;
		SymbolicValue object = null;
		InstanceFieldRef instanceField = null;
		StaticFieldRef staticField = null;
		BinopExpr BinOpRightOp = null;
		Value callerRightOp = null;
		InvokeExpr invExprCaller = null;
		Collection<Unit> callers = null;
		InvokeStmt invStmtCaller = null;
		AssignStmt assignCaller = null;
		ContextualValues contextualValues = null;
		List<SymbolicValue> values = null;

		if(rightOp instanceof InvokeExpr) {
			rightOpInvExpr = (InvokeExpr) rightOp;
			method = rightOpInvExpr.getMethod();
			args = rightOpInvExpr.getArgs();
			if(rightOp instanceof InstanceInvokeExpr) {
				base = ((InstanceInvokeExpr)rightOpInvExpr).getBase();
			}
			object = new MethodRepresentationValue(base, args, method, this.se);
			this.nmrh.recognizeNumericMethod(method, base, object);
		}else if(rightOp instanceof InstanceFieldRef) {
			instanceField = (InstanceFieldRef) rightOp;
			base = instanceField.getBase();
			object = new FieldValue(base, instanceField.getField().getName(), this.se);
			Utils.propagateTags(base, object, this.se);
		}else if(rightOp instanceof BinopExpr){
			BinOpRightOp = (BinopExpr) rightOp;
			binOp1 = BinOpRightOp.getOp1();
			binOp2 = BinOpRightOp.getOp2();
			object = new BinOpValue(this.se, binOp1, binOp2, BinOpRightOp.getSymbol());
			Utils.propagateTags(binOp1, object, this.se);
			Utils.propagateTags(binOp2, object, this.se);
		}else if(rightOp instanceof ParameterRef){
			callers = this.icfg.getCallersOf(this.icfg.getMethodOf(defUnit));
			for(Unit caller : callers) {
				if(caller instanceof InvokeStmt) {
					invStmtCaller = (InvokeStmt) caller;
					invExprCaller = invStmtCaller.getInvokeExpr();
				}else if(caller instanceof AssignStmt) {
					assignCaller = (AssignStmt) caller;
					callerRightOp = assignCaller.getRightOp();
					if(callerRightOp instanceof InvokeExpr) {
						invExprCaller = (InvokeExpr)callerRightOp;
					}else if(callerRightOp instanceof InvokeStmt) {
						invExprCaller = ((InvokeStmt)callerRightOp).getInvokeExpr();
					}
				}
				contextualValues = this.se.getContext().get(invExprCaller.getArg(((ParameterRef) rightOp).getIndex()));
				values = null;
				if(contextualValues == null) {
					results.add(new Pair<Value, SymbolicValue>(leftOp, new UnknownValue(this.se)));
				}else {
					values = contextualValues.getLastCoherentValues(caller);
					if(values != null) {
						for(SymbolicValue sv : values) {
							results.add(new Pair<Value, SymbolicValue>(leftOp, sv));
						}
					}
				}
			}
		}else if(leftOp instanceof StaticFieldRef) {
			staticField = (StaticFieldRef) leftOp;
			object = new FieldValue(base, staticField.getField().getName(), this.se);
			Utils.propagateTags(rightOp, object, this.se);
		}else {
			return results;
		}
		if(object != null) {
			results.add(new Pair<Value, SymbolicValue>(leftOp, object));
		}
		return results;
	}

	@Override
	public void handleInvokeTag(List<Value> args, Value base, SymbolicValue object, SootMethod method) {}
}
