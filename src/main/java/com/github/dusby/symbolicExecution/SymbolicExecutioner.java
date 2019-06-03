package com.github.dusby.symbolicExecution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.github.dusby.predicates.ConjunctionPredicate;
import com.github.dusby.predicates.DisjunctionPredicate;
import com.github.dusby.predicates.Predicate;
import com.github.dusby.predicates.PredicateProvider;
import com.github.dusby.symbolicExecution.symbolicValues.SymbolicValueProvider;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.IfStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;

public class SymbolicExecutioner {
	private final InfoflowCFG icfg;
	private List<Unit> visitedNodes;
	private List<SootMethod> visitedMethods;
	private Map<Value, SymbolicValueProvider> symbolicExecutionResults;
	private Map<SootMethod, ConjunctionPredicate> methodToCurrentPathPredicate;
	private Map<Unit, DisjunctionPredicate> unitToFullPathPredicate;
	private LinkedList<SootMethod> methodWorkList;

	public SymbolicExecutioner(InfoflowCFG icfg, SootMethod mainMethod) {
		this.icfg = icfg;
		this.visitedNodes = new ArrayList<Unit>();
		this.visitedMethods = new ArrayList<SootMethod>();
		this.symbolicExecutionResults = new HashMap<Value, SymbolicValueProvider>();
		this.methodToCurrentPathPredicate = new HashMap<SootMethod, ConjunctionPredicate>();
		this.unitToFullPathPredicate = new HashMap<Unit, DisjunctionPredicate>();
		this.methodWorkList = new LinkedList<SootMethod>();
		this.methodWorkList.add(mainMethod);
	}
	
	public void execute() {
		long startOfExecution = System.currentTimeMillis();
		while(!this.methodWorkList.isEmpty()) {
			SootMethod methodToAnalyze = this.methodWorkList.removeFirst();
			if(!this.visitedMethods.contains(methodToAnalyze)) {
				this.visitedMethods.add(methodToAnalyze);
				Unit entryPoint = this.icfg.getStartPointsOf(methodToAnalyze).iterator().next();
				this.processNode(entryPoint);
			}
		}
		long elapsedTime = System.currentTimeMillis() - startOfExecution;
		org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger("toto");
		logger.debug("tototto");
		System.out.println("Symbolic execution : " + elapsedTime+ "ms");
		
	}

	private void processNode(Unit unit) {
		if(!this.visitedNodes.contains(unit)) { 
			this.visitedNodes.add(unit);
			SootMethod methodBeingAnalyzed = this.icfg.getMethodOf(unit);
			ConjunctionPredicate currentPathPredicate = this.methodToCurrentPathPredicate.get(methodBeingAnalyzed);
			// TODO change string tag
			if(currentPathPredicate == null) {
				this.methodToCurrentPathPredicate.put(methodBeingAnalyzed, new ConjunctionPredicate());
			}else if(!currentPathPredicate.isEmpty()) {
				DisjunctionPredicate unitFullPathPredicate = this.unitToFullPathPredicate.get(unit);
				if(unitFullPathPredicate == null) {
					unitFullPathPredicate = new DisjunctionPredicate((PredicateProvider) new ConjunctionPredicate(currentPathPredicate));
					this.unitToFullPathPredicate.put(unit, unitFullPathPredicate);
				}else {
					unitFullPathPredicate.addPredicate((PredicateProvider) new ConjunctionPredicate(currentPathPredicate));
				}
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
			this.processNode(successor);
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
			if(callee.getDeclaringClass().isApplicationClass()) {
				this.methodWorkList.add(callee);
			}
		}
	}

	public Map<Value, SymbolicValueProvider> getModelContext() {
		return this.symbolicExecutionResults;
	}

	public InfoflowCFG getIcfg() {
		return this.icfg;
	}

	public Map<Unit, DisjunctionPredicate> getUnitToFullPathPredicate() {
		return unitToFullPathPredicate;
	}
	
	
}
