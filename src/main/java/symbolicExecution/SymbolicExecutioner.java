package symbolicExecution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import predicates.ConjunctionPredicate;
import predicates.Predicate;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.ConditionExpr;
import soot.jimple.DefinitionStmt;
import soot.jimple.IfStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;
import soot.tagkit.AnnotationTag;
import symbolicExecution.symbolicValues.SymbolicValueProvider;
import symbolicExecution.typeRecognizers.RecognizerProcessor;
import symbolicExecution.typeRecognizers.StringRecognizer;

public class SymbolicExecutioner {
	private final InfoflowCFG icfg;
	private List<Unit> visitedNodes;
	private Map<Value, SymbolicValueProvider> modelContext;
	private ConjunctionPredicate currentPathPredicate;

	public SymbolicExecutioner(InfoflowCFG icfg) {
		this.icfg = icfg;
		this.visitedNodes = new ArrayList<Unit>();
		this.modelContext = new HashMap<Value, SymbolicValueProvider>();
		this.currentPathPredicate = new ConjunctionPredicate();
		//		for(Entry<Value, SymbolicValueProvider> entry : this.modelContext.entrySet()) {
		//			System.out.println("- "+entry.getKey());
		//			if(entry.getValue()!=null)
		//				System.out.println("- "+entry.getValue().getContextValue());
		//			System.out.println("================");
		//		}
	}

	public void execute(Unit unit) {
		if(!this.visitedNodes.contains(unit)) { 
			this.visitedNodes.add(unit);
			// TODO change string tag
			unit.addTag(new AnnotationTag(this.currentPathPredicate.toString()));
			
			if(unit instanceof InvokeStmt) {
				this.propagateTargetMethod(unit);
			}else if(unit instanceof DefinitionStmt) {
				DefinitionStmt defUnit = (DefinitionStmt) unit;
				// Chain of relevant object recognizers
				RecognizerProcessor rp = new StringRecognizer(null, this);
				Map<Value, SymbolicValueProvider> defModelResult = rp.recognize(defUnit);
				if(defModelResult != null) {
					this.modelContext.putAll(defModelResult);
				}

				if(defUnit.getRightOp() instanceof InvokeExpr) {
					this.propagateTargetMethod(defUnit);
				}
			}

			// Process all successors
			this.processSuccessors(unit, this.icfg.getSuccsOf(unit));
		}
	}
	
	private void processSuccessors(Unit unit, List<Unit> successors) {
		ConditionExpr condExpr = null;
		IfStmt ifStmt = null;
		Predicate lastPredicate = (Predicate)this.currentPathPredicate.getLastPredicate();
		for(Unit successor : successors) {
			if(unit instanceof IfStmt) {
				ifStmt = (IfStmt) unit;
				condExpr = (ConditionExpr) ifStmt.getCondition();
			}
			if(ifStmt != null) {
				if(successor == ifStmt.getTarget()) {
					this.updatePathPredicate(lastPredicate, condExpr, true);
				}else {
					this.updatePathPredicate(lastPredicate, condExpr, false);
				}
			}
			this.execute(successor);
		}
		this.currentPathPredicate.deleteLastPredicate();
	}
	

	private void updatePathPredicate(Predicate lastPredicate, ConditionExpr condExpr, boolean branch) {
		if(lastPredicate != null) {
			ConditionExpr lastPredicateConditionExpression = lastPredicate.getConditionExpression();
			if(lastPredicateConditionExpression == condExpr) {
				this.currentPathPredicate.deleteLastPredicate();
			}
		}
		this.currentPathPredicate.addPredicate(new Predicate(condExpr, branch));
	}

	private void propagateTargetMethod(Unit invokation) {
		Collection<SootMethod> pointsToRightOp = this.icfg.getCalleesOfCallAt(invokation);
		for(SootMethod callee : pointsToRightOp) {
			if(!callee.isJavaLibraryMethod()) {
				this.execute(this.icfg.getStartPointsOf(callee).iterator().next());
			}
		}
	}

	public Map<Value, SymbolicValueProvider> getModelContext() {
		return this.modelContext;
	}

	public InfoflowCFG getIcfg() {
		return this.icfg;
	}
}
