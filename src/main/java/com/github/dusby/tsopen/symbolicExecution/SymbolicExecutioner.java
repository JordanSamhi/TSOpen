package com.github.dusby.tsopen.symbolicExecution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Literal;
import org.logicng.transformations.DistributiveSimplifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.profiler.Profiler;

import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValueProvider;

import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.ConditionExpr;
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
	private Map<Unit, Formula> nodeToAllPossiblePathPredicate;
	private LinkedList<SootMethod> methodWorkList;
	private Map<Literal, ConditionExpr> literalToCondition;

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private Profiler executeProfiler = new Profiler(this.getClass().getName());

	public SymbolicExecutioner(InfoflowCFG icfg, SootMethod mainMethod) {
		this.icfg = icfg;
		this.visitedNodes = new ArrayList<Unit>();
		this.visitedMethods = new ArrayList<SootMethod>();
		this.symbolicExecutionResults = new HashMap<Value, SymbolicValueProvider>();
		this.nodeToAllPossiblePathPredicate = new HashMap<Unit, Formula>();
		this.literalToCondition = new HashMap<Literal, ConditionExpr>();
		this.methodWorkList = new LinkedList<SootMethod>();
		this.methodWorkList.add(mainMethod);
	}

	public void execute() {
		executeProfiler.start("execute");
		FormulaFactory formulaFactory = null;
		SootMethod methodToAnalyze = null;
		Unit entryPoint = null;
		while(!this.methodWorkList.isEmpty()) {
			methodToAnalyze = this.methodWorkList.removeFirst();
			formulaFactory = new FormulaFactory();
			this.visitedMethods.add(methodToAnalyze);
			entryPoint = this.icfg.getStartPointsOf(methodToAnalyze).iterator().next();
			this.processNode(entryPoint, null, formulaFactory, this.shouldBePathSensitive(methodToAnalyze));
		}
		executeProfiler.stop();
		this.logger.info("Symbolic execution : {} ms", TimeUnit.MILLISECONDS.convert(executeProfiler.elapsedTime(), TimeUnit.NANOSECONDS));
	}

	private void processNode(Unit node, Formula currentNodePathPredicate, FormulaFactory formulaFactory, boolean pathSensitive) {
		DefinitionStmt defUnit = null;
		if(!this.visitedNodes.contains(node)) {
			this.visitedNodes.add(node);
			if(node instanceof InvokeStmt) {
				this.propagateTargetMethod(node);
			}else if(node instanceof DefinitionStmt) {
				defUnit = (DefinitionStmt) node;
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
			this.processSuccessors(node, this.icfg.getSuccsOf(node), currentNodePathPredicate, formulaFactory, pathSensitive);
		}
	}

	private void processSuccessors(Unit node, List<Unit> successors, Formula currentNodePathPredicate, FormulaFactory formulaFactory, boolean pathSensitive) {
		Formula successorPathPredicate = null;
		Literal l = null;
		IfStmt ifStmt = null;
		String condition = null;
		Formula possiblePathPredicates = null;
		DistributiveSimplifier simplifier = new DistributiveSimplifier();

		for(Unit successor : successors) {
			if(node instanceof IfStmt) {
				ifStmt = (IfStmt) node;
				condition = String.format("([%s] => %s)", ifStmt.hashCode(), ifStmt.getCondition().toString());
				if(successor == ifStmt.getTarget()) {
					l = formulaFactory.literal(condition, true);
				}else {
					l = formulaFactory.literal(condition, false);
				}
				this.literalToCondition.put(l, (ConditionExpr) ifStmt.getCondition());
				if(currentNodePathPredicate == null) {
					successorPathPredicate = l;

				}else {
					successorPathPredicate = formulaFactory.and(currentNodePathPredicate, l);
				}
			}else {
				successorPathPredicate = currentNodePathPredicate;
			}
			if(successorPathPredicate != null) {
				possiblePathPredicates = this.nodeToAllPossiblePathPredicate.get(successor);
				if(possiblePathPredicates == null) {
					possiblePathPredicates = successorPathPredicate;
				}else {
					possiblePathPredicates = formulaFactory.or(possiblePathPredicates, successorPathPredicate);
				}
				if(!possiblePathPredicates.isConstantFormula()) {
					this.nodeToAllPossiblePathPredicate.put(successor, simplifier.apply(possiblePathPredicates, true));
				}else {
					this.nodeToAllPossiblePathPredicate.remove(successor);
				}
			}
			this.processNode(successor, successorPathPredicate, formulaFactory, pathSensitive);
		}
		if(pathSensitive) {
			this.visitedNodes.remove(node);
		}
	}

	private void propagateTargetMethod(Unit invokation) {
		Collection<SootMethod> pointsTo = this.icfg.getCalleesOfCallAt(invokation);
		for(SootMethod callee : pointsTo) {
			if(callee.getDeclaringClass().isApplicationClass()) {
				if(!this.visitedMethods.contains(callee)) {
					this.visitedMethods.add(callee);
					this.methodWorkList.add(callee);
				}
			}
		}
	}

	private boolean shouldBePathSensitive(SootMethod method) {
		return !method.getName().startsWith("dummyMainMethod");
	}

	public Map<Value, SymbolicValueProvider> getModelContext() {
		return this.symbolicExecutionResults;
	}
}
