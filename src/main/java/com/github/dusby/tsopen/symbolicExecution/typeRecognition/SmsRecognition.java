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
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;
import soot.tagkit.StringConstantValueTag;

public class SmsRecognition extends TypeRecognitionHandler {

	public SmsRecognition(TypeRecognitionHandler next, SymbolicExecution se, InfoflowCFG icfg) {
		super(next, se, icfg);
		this.authorizedTypes.add(Constants.ANDROID_TELEPHONY_SMSMESSAGE);
	}

	@Override
	public void handleConstructorTag(List<Value> args, ObjectValue object) {}

	@Override
	public List<Pair<Value, SymbolicValue>> handleDefinitionStmt(DefinitionStmt defUnit) {
		Value leftOp = defUnit.getLeftOp(),
				rightOp = defUnit.getRightOp();
		String methodName = null;
		List<Pair<Value, SymbolicValue>> results = new LinkedList<Pair<Value,SymbolicValue>>();
		StaticInvokeExpr rightOpStaticInvokeExpr = null;
		SootMethod method = null;
		List<Value> args = null;
		ObjectValue object = null;
		SootClass declaringClass = null;

		if(rightOp instanceof StaticInvokeExpr) {
			rightOpStaticInvokeExpr = (StaticInvokeExpr) rightOp;
			method = rightOpStaticInvokeExpr.getMethod();
			methodName = method.getName();
			declaringClass = method.getDeclaringClass();
			args = rightOpStaticInvokeExpr.getArgs();
			object = new ObjectValue(method.getDeclaringClass().getType(), args, this.se);
			if(methodName.equals(Constants.CREATE_FROM_PDU) && declaringClass.getName().equals(Constants.ANDROID_TELEPHONY_SMSMESSAGE)) {
				object.addTag(new StringConstantValueTag(Constants.SMS_TAG));
			}
			results.add(new Pair<Value, SymbolicValue>(leftOp, object));
		}
		return results;
	}

}
