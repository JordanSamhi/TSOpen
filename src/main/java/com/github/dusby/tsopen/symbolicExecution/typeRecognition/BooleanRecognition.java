package com.github.dusby.tsopen.symbolicExecution.typeRecognition;

import java.util.LinkedList;
import java.util.List;

import org.javatuples.Pair;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;
import com.github.dusby.tsopen.symbolicExecution.methodRecognizers.bool.AfterRecognition;
import com.github.dusby.tsopen.symbolicExecution.methodRecognizers.bool.BeforeRecognition;
import com.github.dusby.tsopen.symbolicExecution.methodRecognizers.bool.BooleanMethodsRecognitionHandler;
import com.github.dusby.tsopen.symbolicExecution.methodRecognizers.bool.ContainsRecognition;
import com.github.dusby.tsopen.symbolicExecution.methodRecognizers.bool.EndsWithRecognition;
import com.github.dusby.tsopen.symbolicExecution.methodRecognizers.bool.EqualsRecognition;
import com.github.dusby.tsopen.symbolicExecution.methodRecognizers.bool.MatchesRecognition;
import com.github.dusby.tsopen.symbolicExecution.methodRecognizers.bool.StartsWithRecognition;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.FieldValue;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.MethodRepresentationValue;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.ObjectValue;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import com.github.dusby.tsopen.utils.Constants;
import com.github.dusby.tsopen.utils.Utils;

import soot.SootMethod;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;

public class BooleanRecognition extends TypeRecognitionHandler {

	private BooleanMethodsRecognitionHandler bmrh;

	public BooleanRecognition(TypeRecognitionHandler next, SymbolicExecution se, InfoflowCFG icfg) {
		super(next, se, icfg);
		this.authorizedTypes.add(Constants.BOOLEAN);
		this.bmrh = new AfterRecognition(null, se);
		this.bmrh = new BeforeRecognition(this.bmrh, se);
		this.bmrh = new EqualsRecognition(this.bmrh, se);
		this.bmrh = new ContainsRecognition(this.bmrh, se);
		this.bmrh = new StartsWithRecognition(this.bmrh, se);
		this.bmrh = new EndsWithRecognition(this.bmrh, se);
		this.bmrh = new MatchesRecognition(this.bmrh, se);
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
		InstanceFieldRef field = null;
		SootMethod method = null;
		List<Value> args = null;
		SymbolicValue object = null;

		if(rightOp instanceof InstanceInvokeExpr) {
			rightOpInvExpr = (InstanceInvokeExpr) rightOp;
			method = rightOpInvExpr.getMethod();
			args = rightOpInvExpr.getArgs();
			base = rightOpInvExpr.getBase();
			object = new MethodRepresentationValue(base, args, method, this.se);
			this.bmrh.recognizeBooleanMethod(method, base, object, args);
		}else if(rightOp instanceof InstanceFieldRef){
			field = (InstanceFieldRef) rightOp;
			base = field.getBase();
			object = new FieldValue(base, field.getField().getName(), this.se);
			Utils.propagateTags(rightOp, object, this.se);
		}else {
			return results;
		}
		results.add(new Pair<Value, SymbolicValue>(leftOp, object));
		return results;
	}

	@Override
	public void handleInvokeTag(List<Value> args, Value base, SymbolicValue object, SootMethod method) {}
}
