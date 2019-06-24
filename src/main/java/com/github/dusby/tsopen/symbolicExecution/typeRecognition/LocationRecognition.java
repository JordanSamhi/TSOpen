package com.github.dusby.tsopen.symbolicExecution.typeRecognition;

import java.util.LinkedList;
import java.util.List;

import org.javatuples.Pair;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.ObjectValue;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import com.github.dusby.tsopen.utils.Constants;

import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;
import soot.tagkit.StringConstantValueTag;

public class LocationRecognition extends TypeRecognitionHandler {

	public LocationRecognition(TypeRecognitionHandler next, SymbolicExecution se, InfoflowCFG icfg) {
		super(next, se, icfg);
		this.authorizedTypes.add(Constants.ANDROID_LOCATION_LOCATION);
	}

	@Override
	public void handleConstructorTag(List<Value> args, ObjectValue object) {}

	@Override
	public List<Pair<Value, SymbolicValue>> handleDefinitionStmt(DefinitionStmt defUnit) {
		Value leftOp = defUnit.getLeftOp(),
				rightOp = defUnit.getRightOp(),
				base = null;
		String methodName = null;
		List<Pair<Value, SymbolicValue>> results = new LinkedList<Pair<Value,SymbolicValue>>();
		InvokeExpr rightOpInvExpr = null;
		SootMethod method = null;
		SootClass declaringClass = null;
		List<Value> args = null;
		ObjectValue object = null;
		Type type = null;

		if(rightOp instanceof InvokeExpr) {
			rightOpInvExpr = (InvokeExpr) rightOp;

			method = rightOpInvExpr.getMethod();
			declaringClass = method.getDeclaringClass();
			methodName = method.getName();
			args = rightOpInvExpr.getArgs();

			if(rightOpInvExpr instanceof StaticInvokeExpr) {
				type = declaringClass.getType();
			}else if(rightOpInvExpr instanceof InstanceInvokeExpr){
				base = ((InstanceInvokeExpr)rightOpInvExpr).getBase();
				type = base.getType();
			}

			object = new ObjectValue(type, args, this.se);
			if((declaringClass.getName().equals(Constants.ANDROID_LOCATION_LOCATION_MANAGER) && methodName.equals(Constants.GET_LAST_KNOW_LOCATION))
					|| (base != null && type.toString().equals(Constants.COM_GOOGLE_ANDROID_GMS_LOCATION_LOCATION_RESULT) && methodName.equals(Constants.GET_LAST_LOCATION))) {
				object.addTag(new StringConstantValueTag(Constants.HERE_TAG));
			}
			results.add(new Pair<Value, SymbolicValue>(leftOp, object));
		}
		return results;
	}
}
