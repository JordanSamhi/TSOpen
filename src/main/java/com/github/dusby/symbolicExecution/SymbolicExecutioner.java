package com.github.dusby.symbolicExecution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.profiler.Profiler;

import com.github.dusby.predicates.JoinPathPredicate;
import com.github.dusby.predicates.PathPredicate;
import com.github.dusby.predicates.Predicate;
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
	private Map<SootMethod, PathPredicate> methodToCurrentPathPredicate;
	private Map<Unit, JoinPathPredicate> nodeToAllPossiblePathPredicate;
	private LinkedList<SootMethod> methodWorkList;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public SymbolicExecutioner(InfoflowCFG icfg, SootMethod mainMethod) {
		this.icfg = icfg;
		this.visitedNodes = new ArrayList<Unit>();
		this.visitedMethods = new ArrayList<SootMethod>();
		this.symbolicExecutionResults = new HashMap<Value, SymbolicValueProvider>();
		this.methodToCurrentPathPredicate = new HashMap<SootMethod, PathPredicate>();
		this.nodeToAllPossiblePathPredicate = new HashMap<Unit, JoinPathPredicate>();
		this.methodWorkList = new LinkedList<SootMethod>();
		this.methodWorkList.add(mainMethod);
	}

	public void execute() {
		Profiler executeProfiler = new Profiler("[Method] execute");
		executeProfiler.start("execution");
		while(!this.methodWorkList.isEmpty()) {
			SootMethod methodToAnalyze = this.methodWorkList.removeFirst();
			if(!this.visitedMethods.contains(methodToAnalyze)) {
				this.visitedMethods.add(methodToAnalyze);
				Unit entryPoint = this.icfg.getStartPointsOf(methodToAnalyze).iterator().next();
				this.processNode(entryPoint, methodToAnalyze);
			}
		}
		executeProfiler.stop();
		this.logger.info("Symbolic execution : {} ms", TimeUnit.MILLISECONDS.convert(executeProfiler.elapsedTime(), TimeUnit.NANOSECONDS));
	}

	private void processNode(Unit node, SootMethod methodToAnalyze) {
		this.updateJoinPathPredicate(node, methodToAnalyze);
		if(!this.visitedNodes.contains(node)) {
			this.visitedNodes.add(node);
			if(node instanceof InvokeStmt) {
				this.propagateTargetMethod(node);
			}else if(node instanceof DefinitionStmt) {
				DefinitionStmt defUnit = (DefinitionStmt) node;
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
			PathPredicate currentPathPredicate = this.methodToCurrentPathPredicate.get(methodToAnalyze);
			this.processSuccessors(node, this.icfg.getSuccsOf(node), currentPathPredicate, methodToAnalyze);
		}
	}

	private void updateJoinPathPredicate(Unit node, SootMethod methodToAnalyze) {
		JoinPathPredicate unitPossiblePaths = this.nodeToAllPossiblePathPredicate.get(node);
		PathPredicate currentPathPredicate = this.methodToCurrentPathPredicate.get(methodToAnalyze);
		if(currentPathPredicate == null) {
			this.methodToCurrentPathPredicate.put(methodToAnalyze, new PathPredicate());
			currentPathPredicate = this.methodToCurrentPathPredicate.get(methodToAnalyze);
		}
		if(unitPossiblePaths == null) {
			unitPossiblePaths = new JoinPathPredicate(new PathPredicate(currentPathPredicate));
			this.nodeToAllPossiblePathPredicate.put(node, unitPossiblePaths);
		}else {
			PathPredicate cp = new PathPredicate(currentPathPredicate);
			if(unitPossiblePaths.isEmpty() || !unitPossiblePaths.isRedundant(cp)) {
				unitPossiblePaths.addPredicate(cp);
			}
		}
	}

	private void processSuccessors(Unit node, List<Unit> successors, PathPredicate currentPathPredicate, SootMethod methodToAnalyze) {
		for(Unit successor : successors) {
			if(node instanceof IfStmt) {
				IfStmt ifStmt = (IfStmt) node;
				if(successor == ifStmt.getTarget()) {
					this.updatePathPredicate(ifStmt, true, currentPathPredicate);
				}else {
					this.updatePathPredicate(ifStmt, false, currentPathPredicate);
				}
			}
			this.processNode(successor, methodToAnalyze);
		}
		if(node instanceof IfStmt) {
			currentPathPredicate.deleteLastPredicate();
		}
		this.visitedNodes.remove(node);
	}


	private void updatePathPredicate(IfStmt ifStmt, boolean branch, PathPredicate currentPathPredicate) {
		Predicate lastPredicate = (Predicate)currentPathPredicate.getLastPredicate();
		if(lastPredicate != null) {
			if(lastPredicate.getIfStmt().equals(ifStmt)) {
				currentPathPredicate.deleteLastPredicate();
			}
		}
		Predicate p = new Predicate(ifStmt, branch);
		if(currentPathPredicate.isEmpty() || !currentPathPredicate.isRedundant(p)) {
			currentPathPredicate.addPredicate(p);
		}
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

	public Map<Unit, JoinPathPredicate> getUnitToFullPathPredicate() {
		return nodeToAllPossiblePathPredicate;
	}


}
