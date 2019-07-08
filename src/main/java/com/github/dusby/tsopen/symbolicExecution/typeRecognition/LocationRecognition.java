package com.github.dusby.tsopen.symbolicExecution.typeRecognition;

import java.util.LinkedList;
import java.util.List;

import org.javatuples.Pair;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;
import com.github.dusby.tsopen.symbolicExecution.methodRecognizers.location.GetLastKnowLocationRecognition;
import com.github.dusby.tsopen.symbolicExecution.methodRecognizers.location.GetLastLocationRecognition;
import com.github.dusby.tsopen.symbolicExecution.methodRecognizers.location.LocationMethodsRecognitionHandler;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.ObjectValue;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.UnknownValue;
import com.github.dusby.tsopen.utils.Constants;

import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
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
						object = new UnknownValue(this.se);
						object.addTag(new StringConstantValueTag(Constants.HERE_TAG));
					}
				}
			}
		}
		results.add(new Pair<Value, SymbolicValue>(leftOp, object));
		return results;
	}

	@Override
	public void handleInvokeTag(List<Value> args, Value base, SymbolicValue object, SootMethod method) {}
}
