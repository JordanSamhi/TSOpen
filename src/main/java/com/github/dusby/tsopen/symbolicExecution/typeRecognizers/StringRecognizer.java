package com.github.dusby.tsopen.symbolicExecution.typeRecognizers;

import java.util.LinkedList;
import java.util.List;

import org.javatuples.Pair;

import com.github.dusby.tsopen.symbolicExecution.ContextualValues;
import com.github.dusby.tsopen.symbolicExecution.SymbolicExecutioner;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.ConcreteValue;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValueProvider;

import soot.Local;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
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

	public StringRecognizer(RecognizerProcessor next, SymbolicExecutioner se, InfoflowCFG icfg) {
		super(next, se, icfg);
		this.authorizedTypes.add("java.lang.String");
		this.authorizedTypes.add("java.lang.StringBuilder");
		this.authorizedTypes.add("java.lang.StringBuffer");
	}

	@Override
	public List<Pair<Value, SymbolicValueProvider>> processRecognition(Unit node) {
		Value leftOp = null,
				rightOp = null;
		String leftOpType = null;
		DefinitionStmt defUnit = null;
		InvokeExpr rightOpInvokeExpr = null;
		SootMethod m = null;
		List<Value> args = null;
		Value base = null;
		Value arg = null;
		List<Pair<Value, SymbolicValueProvider>> results = new LinkedList<Pair<Value,SymbolicValueProvider>>();
		List<SymbolicValueProvider> values = null;
		CastExpr rightOpExpr = null;
		ContextualValues contextualValues = null;

		//TODO propagate values on each node

		if(node instanceof DefinitionStmt) {
			defUnit = (DefinitionStmt) node;
			leftOp = defUnit.getLeftOp();
			rightOp = defUnit.getRightOp();
			leftOpType = leftOp.getType().toQuotedString();
			if(this.isAuthorizedType(leftOpType)) {
				if(rightOp instanceof StringConstant) {
					results.add(new Pair<Value, SymbolicValueProvider>(leftOp, new ConcreteValue((StringConstant)rightOp)));
				}else if(rightOp instanceof ParameterRef) {
					results.add(new Pair<Value, SymbolicValueProvider>(leftOp, new ConcreteValue(StringConstant.v(String.format("%s_p%d", this.icfg.getMethodOf(defUnit).getName(), ((ParameterRef)rightOp).getIndex())))));
				}else if(rightOp instanceof Local) {
					values = this.se.getContext().get(rightOp).getLastValues();
					for(SymbolicValueProvider svp : values) {
						results.add(new Pair<Value, SymbolicValueProvider>(leftOp, svp));
					}
				}else if (rightOp instanceof CastExpr) {
					rightOpExpr = (CastExpr) rightOp;
					contextualValues = this.se.getContext().get(rightOpExpr.getOp());
					if(contextualValues == null) {
						results.add(new Pair<Value, SymbolicValueProvider>(leftOp, new ConcreteValue(StringConstant.v(UNKNOWN_STRING))));
					}else {
						values = contextualValues.getLastValues();
						for(SymbolicValueProvider svp : values) {
							results.add(new Pair<Value, SymbolicValueProvider>(leftOp, svp));
						}
					}
				}
				else if(rightOp instanceof InvokeExpr) {
					rightOpInvokeExpr = (InvokeExpr) rightOp;
					m = rightOpInvokeExpr.getMethod();
					args = rightOpInvokeExpr.getArgs();
					base = rightOpInvokeExpr instanceof InstanceInvokeExpr ? ((InstanceInvokeExpr) rightOpInvokeExpr).getBase() : null;
					results.add(new Pair<Value, SymbolicValueProvider>(leftOp, new SymbolicValue(base, args, m, this.se)));
				}
			}
		}else if(node instanceof InvokeStmt) {
			InvokeExpr invExprUnit = ((InvokeStmt) node).getInvokeExpr();
			if(invExprUnit instanceof SpecialInvokeExpr) {
				m = invExprUnit.getMethod();
				if(m.isConstructor()) {
					base = ((SpecialInvokeExpr) invExprUnit).getBase();
					if(this.isAuthorizedType(base.getType().toQuotedString())) {
						args = invExprUnit.getArgs();
						if(args.size() == 0) {
							results.add(new Pair<Value, SymbolicValueProvider>(leftOp, new ConcreteValue(StringConstant.v(""))));
						}else {
							arg = args.get(0);
							if(arg instanceof Local) {
								// FIXME NULL POINTER HERE ideas waqf
								values = this.se.getContext().get(rightOp).getLastValues();
								for(SymbolicValueProvider svp : values) {
									results.add(new Pair<Value, SymbolicValueProvider>(leftOp, svp));
								}
							}else if(arg instanceof StringConstant) {
								results.add(new Pair<Value, SymbolicValueProvider>(leftOp, new ConcreteValue((StringConstant)arg)));
							}else {
								results.add(new Pair<Value, SymbolicValueProvider>(leftOp, new ConcreteValue(StringConstant.v(""))));
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

	@Override
	protected boolean isAuthorizedType(String leftOpType) {
		return this.authorizedTypes.contains(leftOpType);
	}
}
