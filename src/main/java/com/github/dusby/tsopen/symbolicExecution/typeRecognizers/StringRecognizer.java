package com.github.dusby.tsopen.symbolicExecution.typeRecognizers;

import java.util.List;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecutioner;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.ConcreteValue;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValueProvider;

import soot.Local;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.NewExpr;
import soot.jimple.ParameterRef;
import soot.jimple.StringConstant;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;

public class StringRecognizer extends RecognizerProcessor{

	public StringRecognizer(RecognizerProcessor next, SymbolicExecutioner se, InfoflowCFG icfg) {
		super(next, se, icfg);
	}

	@Override
	public SymbolicValueProvider processRecognition(DefinitionStmt defUnit, Unit node) {
		Value leftOp = defUnit.getLeftOp(),
			  rightOp = defUnit.getRightOp();
		String leftOpType = leftOp.getType().toQuotedString(); 
		if(leftOpType.equals("java.lang.String")
				|| leftOpType.equals("java.lang.StringBuilder")
				|| leftOpType.equals("java.lang.StringBuffer")) {

			if(rightOp instanceof StringConstant) {
				return new ConcreteValue((StringConstant)rightOp);
			}else if(rightOp instanceof ParameterRef) {
//				this.icfg.getcall
				return new ConcreteValue(StringConstant.v(String.format("%s_p%d", this.icfg.getMethodOf(defUnit).getName(), ((ParameterRef)rightOp).getIndex())));
			}else if(rightOp instanceof Local) {
				return this.se.getContext().get(rightOp).getLastValue();
			}else if(rightOp instanceof NewExpr) {
				// TODO retrieve string in constructor
				return new ConcreteValue(StringConstant.v(""));
			}else if(rightOp instanceof InvokeExpr) {
				InvokeExpr rightOpInvokeExpr = (InvokeExpr) rightOp;
				SootMethod m = rightOpInvokeExpr.getMethod();
				List<Value> args = rightOpInvokeExpr.getArgs();
				Value base = rightOpInvokeExpr instanceof InstanceInvokeExpr ? ((InstanceInvokeExpr) rightOpInvokeExpr).getBase() : null;
				return new SymbolicValue(base, args, m, this.se);
			}
		}
		return null;
	}
}
