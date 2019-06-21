package com.github.dusby.tsopen.symbolicExecution.typeRecognition;

import java.util.LinkedList;
import java.util.List;

import org.javatuples.Pair;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.MethodRepresentationValue;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.ObjectValue;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;

import soot.SootClass;
import soot.SootMethod;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;
import soot.tagkit.StringConstantValueTag;

public class LongRecognition extends TypeRecognitionHandler {

	public LongRecognition(TypeRecognitionHandler next, SymbolicExecution se, InfoflowCFG icfg) {
		super(next, se, icfg);
		this.authorizedTypes.add(LONG);
	}

	@Override
	public void handleConstructorTag(List<Value> args, ObjectValue object) {}

	//TODO Factorize this method in parent and create/call get tag definition
	@Override
	public List<Pair<Value, SymbolicValue>> handleDefinitionStmt(DefinitionStmt defUnit) {
		Value leftOp = defUnit.getLeftOp(),
				rightOp = defUnit.getRightOp(),
				base = null;
		String methodName = null;
		List<Pair<Value, SymbolicValue>> results = new LinkedList<Pair<Value,SymbolicValue>>();
		InstanceInvokeExpr rightOpInvExpr = null;
		SootMethod method = null;
		SootClass declaringClass = null;
		List<Value> args = null;
		MethodRepresentationValue object = null;

		if(rightOp instanceof InstanceInvokeExpr) {
			rightOpInvExpr = (InstanceInvokeExpr) rightOp;

			method = rightOpInvExpr.getMethod();
			declaringClass = method.getDeclaringClass();
			methodName = method.getName();
			args = rightOpInvExpr.getArgs();
			base = rightOpInvExpr.getBase();

			object = new MethodRepresentationValue(base, args, method, this.se);
			if(declaringClass.getName().equals(ANDROID_LOCATION_LOCATION) && methodName.equals(GET_LATITUDE)) {
				object.addTag(new StringConstantValueTag(LATITUDE_TAG));
			}else if(declaringClass.getName().equals(ANDROID_LOCATION_LOCATION) && methodName.equals(GET_LONGITUDE)) {
				object.addTag(new StringConstantValueTag(LONGITUDE_TAG));
			}
			this.logger.debug("{}", object);
			results.add(new Pair<Value, SymbolicValue>(leftOp, object));
		}
		return results;
	}

}
