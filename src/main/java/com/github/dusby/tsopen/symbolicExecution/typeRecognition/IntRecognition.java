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
import soot.jimple.StaticInvokeExpr;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;
import soot.tagkit.StringConstantValueTag;

public class IntRecognition extends TypeRecognitionHandler {

	public IntRecognition(TypeRecognitionHandler next, SymbolicExecution se, InfoflowCFG icfg) {
		super(next, se, icfg);
		this.authorizedTypes.add(INT);
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
		InstanceInvokeExpr rightOpInvExpr = null;
		StaticInvokeExpr rightOpStExpr = null;
		SootMethod method = null;
		SootClass declaringClass = null;
		List<Value> args = null;
		MethodRepresentationValue object = null;

		if(rightOp instanceof InstanceInvokeExpr) {
			rightOpInvExpr = (InstanceInvokeExpr) rightOp;
			method = rightOpInvExpr.getMethod();
			args = rightOpInvExpr.getArgs();
			base = rightOpInvExpr.getBase();

		}else if (rightOp instanceof StaticInvokeExpr){
			rightOpStExpr = (StaticInvokeExpr) rightOp;
			method = rightOpStExpr.getMethod();
			args = rightOpStExpr.getArgs();
		}else {
			return results;
		}
		declaringClass = method.getDeclaringClass();
		methodName = method.getName();
		object = new MethodRepresentationValue(base, args, method, this.se);

		if(this.containsTag(base, NOW_TAG)) {
			if(declaringClass.getName().equals(JAVA_UTIL_DATE) && methodName.equals(GET_MONTH)) {
				object.addTag(new StringConstantValueTag(MONTH_TAG));
			}else if(declaringClass.getName().equals(JAVA_UTIL_DATE) && methodName.equals(GET_MINUTES)) {
				object.addTag(new StringConstantValueTag(MINUTES_TAG));
			}else if (declaringClass.getName().equals(JAVA_UTIL_DATE) && methodName.equals(GET_SECONDS)) {
				object.addTag(new StringConstantValueTag(SECONDS_TAG));
			}
		}
		results.add(new Pair<Value, SymbolicValue>(leftOp, object));
		return results;
	}
}