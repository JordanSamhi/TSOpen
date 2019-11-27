package lu.uni.tsopen.symbolicExecution.typeRecognition;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.javatuples.Pair;

import lu.uni.tsopen.symbolicExecution.SymbolicExecution;
import lu.uni.tsopen.symbolicExecution.methodRecognizers.numeric.NumericMethodsRecognitionHandler;
import lu.uni.tsopen.symbolicExecution.symbolicValues.BinOpValue;
import lu.uni.tsopen.symbolicExecution.symbolicValues.MethodRepresentationValue;
import lu.uni.tsopen.symbolicExecution.symbolicValues.ObjectValue;
import lu.uni.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import lu.uni.tsopen.utils.Utils;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.BinopExpr;
import soot.jimple.CastExpr;
import soot.jimple.Constant;
import soot.jimple.DefinitionStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.NewArrayExpr;
import soot.jimple.ParameterRef;
import soot.jimple.StaticFieldRef;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;

public abstract class NumericRecognition extends TypeRecognitionHandler {

	protected NumericMethodsRecognitionHandler nmrh;

	public NumericRecognition(TypeRecognitionHandler next, SymbolicExecution se, InfoflowCFG icfg) {
		super(next, se, icfg);
	}

	@Override
	public void handleConstructorTag(List<Value> args, ObjectValue object) {}

	@Override
	public List<Pair<Value, SymbolicValue>> handleDefinitionStmt(DefinitionStmt defUnit) {
		Value leftOp = defUnit.getLeftOp(),
				rightOp = defUnit.getRightOp(),
				base = null,
				binOp1 = null,
				binOp2 = null;
		List<Pair<Value, SymbolicValue>> results = new LinkedList<Pair<Value,SymbolicValue>>();
		InvokeExpr rightOpInvExpr = null;
		SootMethod method = null;
		List<Value> args = null;
		SymbolicValue object = null;
		BinopExpr BinOpRightOp = null;
		Value callerRightOp = null;
		InvokeExpr invExprCaller = null;
		Collection<Unit> callers = null;
		InvokeStmt invStmtCaller = null;
		AssignStmt assignCaller = null;

		if(rightOp instanceof InvokeExpr) {
			rightOpInvExpr = (InvokeExpr) rightOp;
			method = rightOpInvExpr.getMethod();
			args = rightOpInvExpr.getArgs();
			if(rightOp instanceof InstanceInvokeExpr) {
				base = ((InstanceInvokeExpr)rightOpInvExpr).getBase();
			}
			object = new MethodRepresentationValue(base, args, method, this.se);
			if(this.nmrh != null) {
				this.nmrh.recognizeNumericMethod(method, base, object);
			}
		}else if(rightOp instanceof InstanceFieldRef) {
			this.checkAndProcessContextValues(rightOp, results, leftOp, defUnit);
		}else if(rightOp instanceof StaticFieldRef) {
			this.checkAndProcessContextValues(rightOp, results, leftOp, defUnit);
		}else if(rightOp instanceof BinopExpr){
			BinOpRightOp = (BinopExpr) rightOp;
			binOp1 = BinOpRightOp.getOp1();
			binOp2 = BinOpRightOp.getOp2();
			object = new BinOpValue(this.se, binOp1, binOp2, BinOpRightOp.getSymbol());
			Utils.propagateTags(binOp1, object, this.se);
			Utils.propagateTags(binOp2, object, this.se);
		}else if(rightOp instanceof ParameterRef){
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
		}else if (rightOp instanceof NewArrayExpr){
			object = new ObjectValue(leftOp.getType(), null, this.se);
		}else if(rightOp instanceof ArrayRef) {
			this.checkAndProcessContextValues(((ArrayRef)rightOp).getBase(), results, leftOp, defUnit);
		}else if(leftOp instanceof StaticFieldRef) {
			this.checkAndProcessContextValues(rightOp, results, leftOp, defUnit);
		}else if(leftOp instanceof InstanceFieldRef && !(rightOp instanceof Constant)) {
			this.checkAndProcessContextValues(rightOp, results, leftOp, defUnit);
		}else if(leftOp instanceof InstanceFieldRef && rightOp instanceof Constant) {
			this.checkAndProcessContextValues(rightOp, results, leftOp, defUnit);
		}else if(rightOp instanceof CastExpr) {
			this.checkAndProcessContextValues(((CastExpr) rightOp).getOp(), results, leftOp, defUnit);
		}else {
			return results;
		}
		if(object != null) {
			results.add(new Pair<Value, SymbolicValue>(leftOp, object));
		}
		return results;
	}

	@Override
	public void handleInvokeTag(List<Value> args, Value base, SymbolicValue object, SootMethod method) {}
}
