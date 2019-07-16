package com.github.dusby.tsopen.symbolicExecution.typeRecognition;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.javatuples.Pair;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;
import com.github.dusby.tsopen.symbolicExecution.methodRecognizers.strings.AppendRecognition;
import com.github.dusby.tsopen.symbolicExecution.methodRecognizers.strings.FormatRecognition;
import com.github.dusby.tsopen.symbolicExecution.methodRecognizers.strings.GetMessageBodyRecognition;
import com.github.dusby.tsopen.symbolicExecution.methodRecognizers.strings.GetOriginatingAddressRecognition;
import com.github.dusby.tsopen.symbolicExecution.methodRecognizers.strings.StringMethodsRecognitionHandler;
import com.github.dusby.tsopen.symbolicExecution.methodRecognizers.strings.SubStringRecognition;
import com.github.dusby.tsopen.symbolicExecution.methodRecognizers.strings.ToLowerCaseRecognition;
import com.github.dusby.tsopen.symbolicExecution.methodRecognizers.strings.ToStringRecognition;
import com.github.dusby.tsopen.symbolicExecution.methodRecognizers.strings.ToUpperCaseRecognition;
import com.github.dusby.tsopen.symbolicExecution.methodRecognizers.strings.ValueOfRecognition;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.ConstantValue;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.MethodRepresentationValue;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.ObjectValue;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import com.github.dusby.tsopen.utils.Constants;
import com.github.dusby.tsopen.utils.Utils;

import soot.Local;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.CastExpr;
import soot.jimple.DefinitionStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.ParameterRef;
import soot.jimple.ReturnStmt;
import soot.jimple.StringConstant;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;

public class StringRecognition extends TypeRecognitionHandler{

	private StringMethodsRecognitionHandler smrh;

	public StringRecognition(TypeRecognitionHandler next, SymbolicExecution se, InfoflowCFG icfg) {
		super(next, se, icfg);
		this.smrh = new AppendRecognition(null, se);
		this.smrh = new ValueOfRecognition(this.smrh, se);
		this.smrh = new ToStringRecognition(this.smrh, se);
		this.smrh = new SubStringRecognition(this.smrh, se);
		this.smrh = new GetMessageBodyRecognition(this.smrh, se);
		this.smrh = new FormatRecognition(this.smrh, se);
		this.smrh = new ToLowerCaseRecognition(this.smrh, se);
		this.smrh = new ToUpperCaseRecognition(this.smrh, se);
		this.smrh = new GetOriginatingAddressRecognition(this.smrh, se);
		this.authorizedTypes.add(Constants.JAVA_LANG_STRING);
		this.authorizedTypes.add(Constants.JAVA_LANG_STRING_BUFFER);
		this.authorizedTypes.add(Constants.JAVA_LANG_STRING_BUILDER);
	}

	@Override
	public List<Pair<Value, SymbolicValue>> handleDefinitionStmt(DefinitionStmt defUnit) {
		Value leftOp = defUnit.getLeftOp(),
				rightOp = defUnit.getRightOp(),
				callerRightOp = null,
				base = null;
		InvokeExpr rightOpInvokeExpr = null,
				invExprCaller = null;
		SootMethod method = null;
		List<Value> args = null;
		List<Pair<Value, SymbolicValue>> results = new LinkedList<Pair<Value,SymbolicValue>>();
		CastExpr rightOpExpr = null;
		Collection<Unit> callers = null;
		InvokeStmt invStmtCaller = null;
		AssignStmt assignCaller = null;
		List<SymbolicValue> recognizedValues = null;
		InstanceFieldRef field = null;
		SymbolicValue object = null;

		if(rightOp instanceof StringConstant) {
			results.add(new Pair<Value, SymbolicValue>(leftOp, new ConstantValue((StringConstant)rightOp, this.se)));
		}else if(rightOp instanceof ParameterRef) {
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
		}else if(rightOp instanceof Local && !(leftOp instanceof InstanceFieldRef)) {
			this.checkAndProcessContextValues(rightOp, results, leftOp, null);
		}else if (rightOp instanceof CastExpr) {
			rightOpExpr = (CastExpr) rightOp;
			this.checkAndProcessContextValues(rightOpExpr.getOp(), results, leftOp, null);
		}else if(rightOp instanceof InvokeExpr) {
			rightOpInvokeExpr = (InvokeExpr) rightOp;
			method = rightOpInvokeExpr.getMethod();
			args = rightOpInvokeExpr.getArgs();
			base = rightOpInvokeExpr instanceof InstanceInvokeExpr ? ((InstanceInvokeExpr) rightOpInvokeExpr).getBase() : null;
			recognizedValues = this.smrh.recognizeStringMethod(method, base, args);
			if(recognizedValues != null) {
				for(SymbolicValue recognizedValue : recognizedValues) {
					results.add(new Pair<Value, SymbolicValue>(leftOp, recognizedValue));
				}
			}else {
				object = new MethodRepresentationValue(base, args, method, this.se);
				results.add(new Pair<Value, SymbolicValue>(leftOp, object));
			}
			if(!this.se.isMethodVisited(method)) {
				this.se.addMethodToWorkList(this.icfg.getMethodOf(defUnit));
			}else {
				if(method.isConcrete()) {
					for(Unit u : method.retrieveActiveBody().getUnits()) {
						if(u instanceof ReturnStmt) {
							if(object != null) {
								Utils.propagateTags(((ReturnStmt)u).getOp(), object, this.se);
							}
						}
					}
				}
			}
		}else if(rightOp instanceof InstanceFieldRef){
			this.checkAndProcessContextValues(rightOp, results, leftOp, defUnit);
		}else if(leftOp instanceof InstanceFieldRef) {
			this.checkAndProcessContextValues(rightOp, results, leftOp, defUnit);
		}
		return results;
	}

	@Override
	public void handleConstructor(InvokeExpr invExprUnit, Value base, List<Pair<Value, SymbolicValue>> results) {
		Value arg = null;
		List<Value> args = invExprUnit.getArgs();
		ConstantValue cv = null;
		if(args.size() == 0) {
			results.add(new Pair<Value, SymbolicValue>(base, new ConstantValue(StringConstant.v(Constants.EMPTY_STRING), this.se)));
		}else {
			arg = args.get(0);
			if(arg instanceof Local) {
				this.checkAndProcessContextValues(arg, results, base, null);
			}else {
				if(arg instanceof StringConstant) {
					cv = new ConstantValue((StringConstant)arg, this.se);
				}
				else {
					cv = new ConstantValue(StringConstant.v(Constants.EMPTY_STRING), this.se);
				}
				results.add(new Pair<Value, SymbolicValue>(base, cv));
			}
		}
	}

	@Override
	public void handleConstructorTag(List<Value> args, ObjectValue object) {}

	@Override
	public void handleInvokeTag(List<Value> args, Value base, SymbolicValue object, SootMethod method) {}
}
