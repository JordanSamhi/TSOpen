package com.github.dusby.tsopen.symbolicExecution.typeRecognition;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.javatuples.Pair;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;
import com.github.dusby.tsopen.symbolicExecution.methodRecognizers.location.GetLastKnowLocationRecognition;
import com.github.dusby.tsopen.symbolicExecution.methodRecognizers.location.GetLastLocationRecognition;
import com.github.dusby.tsopen.symbolicExecution.methodRecognizers.location.LocationMethodsRecognitionHandler;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.ObjectValue;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import com.github.dusby.tsopen.utils.Constants;

import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.DefinitionStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.ParameterRef;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;
import soot.tagkit.StringConstantValueTag;

public class LocationRecognition extends TypeRecognitionHandler {

	private LocationMethodsRecognitionHandler lmrh;

	public LocationRecognition(TypeRecognitionHandler next, SymbolicExecution se, InfoflowCFG icfg) {
		super(next, se, icfg);
		this.authorizedTypes.add(Constants.ANDROID_LOCATION_LOCATION);
		this.lmrh = new GetLastKnowLocationRecognition(null, se);
		this.lmrh = new GetLastLocationRecognition(this.lmrh, se);
	}

	@Override
	public void handleConstructorTag(List<Value> args, ObjectValue object) {}

	@Override
	public List<Pair<Value, SymbolicValue>> handleDefinitionStmt(DefinitionStmt defUnit) {
		Value leftOp = defUnit.getLeftOp(),
				rightOp = defUnit.getRightOp(),
				base = null;
		List<Pair<Value, SymbolicValue>> results = new LinkedList<Pair<Value,SymbolicValue>>();
		InvokeExpr rightOpInvExpr = null;
		SootMethod method = null;
		SootClass declaringClass = null;
		List<Value> args = null;
		SymbolicValue object = null;
		Type type = null;

		Value callerRightOp = null;
		InvokeExpr invExprCaller = null;
		Collection<Unit> callers = null;
		InvokeStmt invStmtCaller = null;
		AssignStmt assignCaller = null;

		if(rightOp instanceof InvokeExpr) {
			rightOpInvExpr = (InvokeExpr) rightOp;

			method = rightOpInvExpr.getMethod();
			declaringClass = method.getDeclaringClass();
			args = rightOpInvExpr.getArgs();

			if(rightOpInvExpr instanceof StaticInvokeExpr) {
				type = declaringClass.getType();
			}else if(rightOpInvExpr instanceof InstanceInvokeExpr){
				base = ((InstanceInvokeExpr)rightOpInvExpr).getBase();
				type = base.getType();
			}

			object = new ObjectValue(type, args, this.se);
			this.lmrh.recognizeLocationMethod(method, object);
		}else if(rightOp instanceof ParameterRef) {
			method = this.icfg.getMethodOf(defUnit);
			declaringClass = method.getDeclaringClass();
			if(method.getName().equals(Constants.ON_LOCATION_CHANGED)) {
				for(SootClass sc : declaringClass.getInterfaces()) {
					if(sc.getName().equals(Constants.ANDROID_LOCATION_LOCATION_LISTENER)) {
						type = method.retrieveActiveBody().getParameterLocal(0).getType();
						object = new ObjectValue(type, null, this.se);
						object.addTag(new StringConstantValueTag(Constants.HERE_TAG));
					}
				}
			}else {
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

					this.checkAndProcessContextValues(invExprCaller.getArg(((ParameterRef) rightOp).getIndex()), results, leftOp, caller);
				}
			}
		}
		if(object != null) {
			results.add(new Pair<Value, SymbolicValue>(leftOp, object));
		}
		return results;
	}

	@Override
	public void handleInvokeTag(List<Value> args, Value base, SymbolicValue object, SootMethod method) {}
}
