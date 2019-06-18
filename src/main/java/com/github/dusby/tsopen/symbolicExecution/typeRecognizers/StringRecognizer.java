package com.github.dusby.tsopen.symbolicExecution.typeRecognizers;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.javatuples.Pair;

import com.github.dusby.tsopen.symbolicExecution.ContextualValues;
import com.github.dusby.tsopen.symbolicExecution.SymbolicExecutioner;
import com.github.dusby.tsopen.symbolicExecution.methodRecognizers.strings.AppendRecognizor;
import com.github.dusby.tsopen.symbolicExecution.methodRecognizers.strings.StringMethodsRecognizerProcessor;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.ConcreteValue;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValueProvider;

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
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StringConstant;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;

public class StringRecognizer extends RecognizerProcessor{

	private static final String UNKNOWN_STRING = "UNKNOWN_STRING";
	private static final String EMPTY_STRING = "";

	private StringMethodsRecognizerProcessor smrp;

	public StringRecognizer(RecognizerProcessor next, SymbolicExecutioner se, InfoflowCFG icfg) {
		super(next, se, icfg);
		this.smrp = new AppendRecognizor(null, se);
		this.authorizedTypes.add("java.lang.String");
		this.authorizedTypes.add("java.lang.StringBuilder");
		this.authorizedTypes.add("java.lang.StringBuffer");
	}

	@Override
	public List<Pair<Value, SymbolicValueProvider>> processRecognition(Unit node) {
		Value leftOp = null,
				rightOp = null,
				callerRightOp = null,
				base = null,
				arg = null;
		InvokeExpr rightOpInvokeExpr = null,
				invExprCaller = null,
				invExprUnit = null;
		String leftOpType = null;
		DefinitionStmt defUnit = null;
		SootMethod m = null;
		List<Value> args = null;
		List<Pair<Value, SymbolicValueProvider>> results = new LinkedList<Pair<Value,SymbolicValueProvider>>();
		CastExpr rightOpExpr = null;
		ContextualValues contextualValues = null;
		Collection<Unit> callers = null;
		InvokeStmt invStmtCaller = null;
		AssignStmt assignCaller = null;
		List<String> methodRecognized = null;

		if(node instanceof DefinitionStmt) {
			defUnit = (DefinitionStmt) node;
			leftOp = defUnit.getLeftOp();
			rightOp = defUnit.getRightOp();
			leftOpType = leftOp.getType().toQuotedString();
			if(this.isAuthorizedType(leftOpType)) {
				if(rightOp instanceof StringConstant) {
					results.add(new Pair<Value, SymbolicValueProvider>(leftOp, new ConcreteValue((StringConstant)rightOp)));
				}else if(rightOp instanceof ParameterRef) {
					callers = this.icfg.getCallersOf(this.icfg.getMethodOf(node));
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
					m = rightOpInvokeExpr.getMethod();
					args = rightOpInvokeExpr.getArgs();
					base = rightOpInvokeExpr instanceof InstanceInvokeExpr ? ((InstanceInvokeExpr) rightOpInvokeExpr).getBase() : null;
					methodRecognized = this.smrp.recognize(m, base, args);
					if(methodRecognized != null) {
						for(String s : methodRecognized) {
							results.add(new Pair<Value, SymbolicValueProvider>(leftOp, new ConcreteValue(StringConstant.v(s))));
						}
					}else {
						results.add(new Pair<Value, SymbolicValueProvider>(leftOp, new SymbolicValue(base, args, m, this.se)));
					}
				}
			}
		}else if(node instanceof InvokeStmt) {
			invExprUnit = ((InvokeStmt) node).getInvokeExpr();
			if(invExprUnit instanceof SpecialInvokeExpr) {
				m = invExprUnit.getMethod();
				if(m.isConstructor()) {
					base = ((SpecialInvokeExpr) invExprUnit).getBase();
					if(this.isAuthorizedType(base.getType().toQuotedString())) {
						args = invExprUnit.getArgs();
						if(args.size() == 0) {
							results.add(new Pair<Value, SymbolicValueProvider>(base, new ConcreteValue(StringConstant.v(EMPTY_STRING))));
						}else {
							arg = args.get(0);
							if(arg instanceof Local) {
								contextualValues = this.se.getContext().get(arg);
								this.checkAndProcessContextValues(contextualValues, results, base);
							}else if(arg instanceof StringConstant) {
								results.add(new Pair<Value, SymbolicValueProvider>(base, new ConcreteValue((StringConstant)arg)));
							}else {
								results.add(new Pair<Value, SymbolicValueProvider>(base, new ConcreteValue(StringConstant.v(EMPTY_STRING))));
							}
						}
					}
				}
			}
		}
		if(results.isEmpty()) {
			return null;
		}else {
			return results;
		}
	}

	private void checkAndProcessContextValues(ContextualValues contextualValues, List<Pair<Value, SymbolicValueProvider>> results, Value leftOp) {
		List<SymbolicValueProvider> values = null;
		if(contextualValues == null) {
			results.add(new Pair<Value, SymbolicValueProvider>(leftOp, new ConcreteValue(StringConstant.v(UNKNOWN_STRING))));
		}else {
			values = contextualValues.getLastValues();
			for(SymbolicValueProvider svp : values) {
				results.add(new Pair<Value, SymbolicValueProvider>(leftOp, svp));
			}
		}
	}

	@Override
	protected boolean isAuthorizedType(String leftOpType) {
		return this.authorizedTypes.contains(leftOpType);
	}
}
