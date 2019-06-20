package com.github.dusby.tsopen.symbolicExecution.typeRecognition;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.javatuples.Pair;

import com.github.dusby.tsopen.symbolicExecution.ContextualValues;
import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;
import com.github.dusby.tsopen.symbolicExecution.methodRecognizers.strings.AppendRecognizer;
import com.github.dusby.tsopen.symbolicExecution.methodRecognizers.strings.StringMethodsRecognizerHandler;
import com.github.dusby.tsopen.symbolicExecution.methodRecognizers.strings.SubStringRecognizer;
import com.github.dusby.tsopen.symbolicExecution.methodRecognizers.strings.ToStringRecognizer;
import com.github.dusby.tsopen.symbolicExecution.methodRecognizers.strings.ValueOfRecognizer;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.ConstantValue;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.MethodRepresentationValue;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.ObjectValue;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;

import soot.Local;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.CastExpr;
import soot.jimple.DefinitionStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.ParameterRef;
import soot.jimple.StringConstant;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;

public class StringRecognition extends TypeRecognitionHandler{

	private StringMethodsRecognizerHandler smrh;

	public StringRecognition(TypeRecognitionHandler next, SymbolicExecution se, InfoflowCFG icfg) {
		super(next, se, icfg);
		this.smrh = new AppendRecognizer(null, se);
		this.smrh = new ValueOfRecognizer(this.smrh, se);
		this.smrh = new ToStringRecognizer(this.smrh, se);
		this.smrh = new SubStringRecognizer(this.smrh, se);
		this.authorizedTypes.add(JAVA_LANG_STRING);
		this.authorizedTypes.add(JAVA_LANG_STRING_BUFFER);
		this.authorizedTypes.add(JAVA_LANG_STRING_BUILDER);
	}

	private void checkAndProcessContextValues(ContextualValues contextualValues, List<Pair<Value, SymbolicValue>> results, Value leftOp) {
		List<SymbolicValue> values = null;
		if(contextualValues == null) {
			results.add(new Pair<Value, SymbolicValue>(leftOp, new ConstantValue(StringConstant.v(UNKNOWN_STRING))));
		}else {
			values = contextualValues.getLastCoherentValues();
			for(SymbolicValue sv : values) {
				results.add(new Pair<Value, SymbolicValue>(leftOp, sv));
			}
		}
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
		ContextualValues contextualValues = null;
		Collection<Unit> callers = null;
		InvokeStmt invStmtCaller = null;
		AssignStmt assignCaller = null;
		List<SymbolicValue> recognizedValues = null;

		if(rightOp instanceof StringConstant) {
			results.add(new Pair<Value, SymbolicValue>(leftOp, new ConstantValue((StringConstant)rightOp)));
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
				contextualValues = this.se.getContext().get(invExprCaller.getArg(((ParameterRef) rightOp).getIndex()));
				this.checkAndProcessContextValues(contextualValues, results, leftOp);
			}
		}else if(rightOp instanceof Local) {
			contextualValues = this.se.getContext().get(rightOp);
			this.checkAndProcessContextValues(contextualValues, results, leftOp);
		}else if (rightOp instanceof CastExpr) {
			rightOpExpr = (CastExpr) rightOp;
			contextualValues = this.se.getContext().get(rightOpExpr.getOp());
			this.checkAndProcessContextValues(contextualValues, results, leftOp);
		}else if(rightOp instanceof InvokeExpr) {
			rightOpInvokeExpr = (InvokeExpr) rightOp;
			method = rightOpInvokeExpr.getMethod();
			args = rightOpInvokeExpr.getArgs();
			base = rightOpInvokeExpr instanceof InstanceInvokeExpr ? ((InstanceInvokeExpr) rightOpInvokeExpr).getBase() : null;
			recognizedValues = this.smrh.recognize(method, base, args);
			if(recognizedValues != null) {
				for(SymbolicValue recognizedValue : recognizedValues) {
					results.add(new Pair<Value, SymbolicValue>(leftOp, recognizedValue));
				}
			}else {
				results.add(new Pair<Value, SymbolicValue>(leftOp, new MethodRepresentationValue(base, args, method, this.se)));
			}
		}
		return results;
	}

	@Override
	public void handleConstructor(InvokeExpr invExprUnit, Value base, List<Pair<Value, SymbolicValue>> results) {
		Value arg = null;
		List<Value> args = invExprUnit.getArgs();
		ContextualValues contextualValues = null;

		if(args.size() == 0) {
			results.add(new Pair<Value, SymbolicValue>(base, new ConstantValue(StringConstant.v(EMPTY_STRING))));
		}else {
			arg = args.get(0);
			if(arg instanceof Local) {
				contextualValues = this.se.getContext().get(arg);
				this.checkAndProcessContextValues(contextualValues, results, base);
			}else if(arg instanceof StringConstant) {
				results.add(new Pair<Value, SymbolicValue>(base, new ConstantValue((StringConstant)arg)));
			}else {
				results.add(new Pair<Value, SymbolicValue>(base, new ConstantValue(StringConstant.v(EMPTY_STRING))));
			}
		}
	}

	@Override
	public void handleConstructorTag(List<Value> args, ObjectValue object) {}


}
