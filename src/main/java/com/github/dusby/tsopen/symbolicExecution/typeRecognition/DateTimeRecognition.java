package com.github.dusby.tsopen.symbolicExecution.typeRecognition;

import java.util.LinkedList;
import java.util.List;

import org.javatuples.Pair;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.ObjectValue;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import com.github.dusby.tsopen.utils.Constants;

import soot.SootMethod;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;
import soot.tagkit.StringConstantValueTag;

public class DateTimeRecognition extends TypeRecognitionHandler {

	public DateTimeRecognition(TypeRecognitionHandler next, SymbolicExecution se, InfoflowCFG icfg) {
		super(next, se, icfg);
		this.authorizedTypes.add(Constants.JAVA_UTIL_DATE);
		this.authorizedTypes.add(Constants.JAVA_UTIL_CALENDAR);
		this.authorizedTypes.add(Constants.JAVA_UTIL_GREGORIAN_CALENDAR);
		this.authorizedTypes.add(Constants.JAVA_TIME_LOCAL_DATE_TIME);
		this.authorizedTypes.add(Constants.JAVA_TIME_LOCAL_DATE);
	}

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
		String className = null;

		if(rightOp instanceof StaticInvokeExpr) {
			rightOpStaticInvokeExpr = (StaticInvokeExpr) rightOp;
			method = rightOpStaticInvokeExpr.getMethod();
			methodName = method.getName();
			className = method.getDeclaringClass().getName();
			args = rightOpStaticInvokeExpr.getArgs();
			object = new ObjectValue(method.getDeclaringClass().getType(), args, this.se);
			if((methodName.equals(Constants.GET_INSTANCE) && (className.equals(Constants.JAVA_UTIL_CALENDAR) || className.equals(Constants.JAVA_UTIL_GREGORIAN_CALENDAR)))
					|| methodName.equals(Constants.NOW) && (className.equals(Constants.JAVA_TIME_LOCAL_DATE_TIME) || className.equals(Constants.JAVA_TIME_LOCAL_DATE))) {
				object.addTag(new StringConstantValueTag(Constants.NOW_TAG));
			}
			results.add(new Pair<Value, SymbolicValue>(leftOp, object));
		}
		return results;
	}

	@Override
	public void handleConstructorTag(List<Value> args, ObjectValue object) {
		if(args.size() == 0) {
			object.addTag(new StringConstantValueTag(Constants.NOW_TAG));
		}
	}
}
