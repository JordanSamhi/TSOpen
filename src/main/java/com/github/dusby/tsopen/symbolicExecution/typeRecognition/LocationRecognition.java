package com.github.dusby.tsopen.symbolicExecution.typeRecognition;

import java.util.LinkedList;
import java.util.List;

import org.javatuples.Pair;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.ObjectValue;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;

import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;
import soot.tagkit.StringConstantValueTag;

public class LocationRecognition extends TypeRecognitionHandler {

	public LocationRecognition(TypeRecognitionHandler next, SymbolicExecution se, InfoflowCFG icfg) {
		super(next, se, icfg);
		this.authorizedTypes.add(ANDROID_LOCATION_LOCATION);
	}

	@Override
	public void handleConstructorTag(List<Value> args, ObjectValue object) {}

	@Override
	public List<Pair<Value, SymbolicValue>> handleDefinitionStmt(DefinitionStmt defUnit) {
		Value leftOp = defUnit.getLeftOp(),
				rightOp = defUnit.getRightOp();
		String methodName = null;
		List<Pair<Value, SymbolicValue>> results = new LinkedList<Pair<Value,SymbolicValue>>();
		InvokeExpr rightOpStaticInvokeExpr = null;
		SootMethod method = null;
		SootClass sootClass = null;
		List<Value> args = null;
		ObjectValue location = null;
		Type type = null;

		if(rightOp instanceof InvokeStmt) {
			rightOpStaticInvokeExpr = ((InvokeStmt)((StaticInvokeExpr) rightOp)).getInvokeExpr();
			method = rightOpStaticInvokeExpr.getMethod();
			sootClass = method.getDeclaringClass();
			methodName = method.getName();

			if(rightOpStaticInvokeExpr instanceof StaticInvokeExpr) {
				type = sootClass.getType();
			}else if(rightOpStaticInvokeExpr instanceof InstanceInvokeExpr){
				type = ((InstanceInvokeExpr)rightOpStaticInvokeExpr).getBase().getType();
			}else {
				type = null;
			}
			args = rightOpStaticInvokeExpr.getArgs();
			location = new ObjectValue(type, args, this.se);
			if(sootClass.getName().equals(ANDROID_LOCATION_LOCATION_MANAGER) && methodName.equals(GET_LAST_KNOW_LOCATION)) {
				location.addTag(new StringConstantValueTag(HERE_TAG));
			}
			results.add(new Pair<Value, SymbolicValue>(leftOp, location));
		}
		return results;
	}
}
