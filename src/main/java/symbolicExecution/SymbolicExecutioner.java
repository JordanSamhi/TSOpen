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
import soot.jimple.DefinitionStmt;
import soot.jimple.IfStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;
import soot.tagkit.AnnotationTag;
import symbolicExecution.symbolicValues.SymbolicValueProvider;

public class SymbolicExecutioner {
	private final InfoflowCFG icfg;
	private List<Unit> visitedNodes;
	private Map<Value, SymbolicValueProvider> symbolicExecutionResults;
	private List<SootMethod> visitedMethods;
	private Map<SootMethod, ConjunctionPredicate> methodPathPredicate;

	public SymbolicExecutioner(InfoflowCFG icfg) {
		this.icfg = icfg;
		this.visitedNodes = new ArrayList<Unit>();
		this.symbolicExecutionResults = new HashMap<Value, SymbolicValueProvider>();
		this.visitedMethods = new ArrayList<SootMethod>();
		this.methodPathPredicate = new HashMap<SootMethod, ConjunctionPredicate>();
	}

	public void execute(Unit unit) {
		if(!this.visitedNodes.contains(unit)) { 
			this.visitedNodes.add(unit);
			SootMethod methodBeingAnalyzed = this.icfg.getMethodOf(unit);
			ConjunctionPredicate currentPathPredicate = this.methodPathPredicate.get(methodBeingAnalyzed);
			// TODO change string tag
			if(currentPathPredicate != null && !currentPathPredicate.isEmpty()) {
				unit.addTag(new AnnotationTag(currentPathPredicate.toString()));
			}else if(currentPathPredicate == null) {
				this.methodPathPredicate.put(methodBeingAnalyzed, new ConjunctionPredicate());
			}
			if(unit instanceof InvokeStmt) {
				this.propagateTargetMethod(unit);
			}else if(unit instanceof DefinitionStmt) {
				DefinitionStmt defUnit = (DefinitionStmt) unit;
				// Chain of relevant object recognizers
//				RecognizerProcessor rp = new StringRecognizer(null, this);
//				Map<Value, SymbolicValueProvider> objectRecognized = rp.recognize(defUnit);
//				if(objectRecognized != null) {
//					this.symbolicExecutionResults.putAll(objectRecognized);
//				}

				if(defUnit.getRightOp() instanceof InvokeExpr) {
					this.propagateTargetMethod(defUnit);
				}
			}
			
			this.processSuccessors(unit, this.icfg.getSuccsOf(unit), currentPathPredicate);
		}
	}
	
	private void processSuccessors(Unit unit, List<Unit> successors, ConjunctionPredicate currentPathPredicate) {
		for(Unit successor : successors) {
			if(unit instanceof IfStmt) {
				IfStmt ifStmt = (IfStmt) unit;
				if(successor == ifStmt.getTarget()) {
					this.updatePathPredicate(ifStmt, true, currentPathPredicate);
				}else {
					this.updatePathPredicate(ifStmt, false, currentPathPredicate);
				}
			}
			this.execute(successor);
		}
		if(unit instanceof IfStmt) {
			currentPathPredicate.deleteLastPredicate();
		}
		this.visitedNodes.remove(unit);
	}
	

	private void updatePathPredicate(IfStmt ifStmt, boolean branch, ConjunctionPredicate currentPathPredicate) {
		Predicate lastPredicate = (Predicate)currentPathPredicate.getLastPredicate();
		if(lastPredicate != null) {
			if(lastPredicate.getIfStmt().equals(ifStmt)) {
				currentPathPredicate.deleteLastPredicate();
			}
		}
		currentPathPredicate.addPredicate(new Predicate(ifStmt, branch));
	}

	private void propagateTargetMethod(Unit invokation) {
		Collection<SootMethod> pointsTo = this.icfg.getCalleesOfCallAt(invokation);
		for(SootMethod callee : pointsTo) {
			Unit startingPoint = this.icfg.getStartPointsOf(callee).iterator().next();
			if(callee.getDeclaringClass().isApplicationClass()) {
				if(!this.visitedMethods.contains(callee)) {
					this.visitedMethods.add(callee);
					this.execute(startingPoint);
				}
			}
		}
	}

	public Map<Value, SymbolicValueProvider> getModelContext() {
		return this.symbolicExecutionResults;
	}

	public InfoflowCFG getIcfg() {
		return this.icfg;
	}

	public Map<SootMethod, ConjunctionPredicate> getMethodPathPredicate() {
		return methodPathPredicate;
	}
	
}
