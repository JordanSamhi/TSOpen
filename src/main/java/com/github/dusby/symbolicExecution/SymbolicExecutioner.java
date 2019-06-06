package com.github.dusby.symbolicExecution;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.profiler.Profiler;

import com.github.dusby.symbolicExecution.symbolicValues.SymbolicValueProvider;

import soot.Scene;
import soot.SootClass;
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
		while(!this.methodWorkList.isEmpty()) {
			SootMethod methodToAnalyze = this.methodWorkList.removeFirst();
			formulaFactory = new FormulaFactory();
			if(!this.visitedMethods.contains(methodToAnalyze)) {
				this.visitedMethods.add(methodToAnalyze);
				Unit entryPoint = this.icfg.getStartPointsOf(methodToAnalyze).iterator().next();
				this.processNode(entryPoint, null, formulaFactory);
			}
		}
		executeProfiler.stop();
		this.logger.info("Symbolic execution : {} ms", TimeUnit.MILLISECONDS.convert(executeProfiler.elapsedTime(), TimeUnit.NANOSECONDS));
		for(SootClass c : Scene.v().getApplicationClasses()) {
			System.out.println("**********");
			System.out.println(c.getName());
			System.out.println("**********");
			for(SootMethod m : c.getMethods()) {
				System.out.println("===========");
				System.out.println(m.getName());
				System.out.println("===========");
				for(Unit u : m.retrieveActiveBody().getUnits()){
					if(this.nodeToAllPossiblePathPredicate.get(u)!=null) {
						System.out.println(u);
						System.out.println(this.nodeToAllPossiblePathPredicate.get(u));
					}
				}
			}
		}
	}

	private void processNode(Unit node, Formula currentNodePathPredicate, FormulaFactory formulaFactory) {
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
			this.processSuccessors(node, this.icfg.getSuccsOf(node), currentNodePathPredicate, formulaFactory);
		}
	}

	private void processSuccessors(Unit node, List<Unit> successors, Formula currentNodePathPredicate, FormulaFactory formulaFactory) {
		Formula successorPathPredicate = null;
		Literal l = null;
		for(Unit successor : successors) {
			if(node instanceof IfStmt) {
				IfStmt ifStmt = (IfStmt) node;
				
				String condition = String.format("([%s] => %s)", ifStmt.hashCode(), ifStmt.getCondition().toString());
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
				Formula possiblePathPredicates = this.nodeToAllPossiblePathPredicate.get(successor);
				if(possiblePathPredicates == null) {
					possiblePathPredicates = successorPathPredicate;
				}else {
					possiblePathPredicates = formulaFactory.or(possiblePathPredicates, successorPathPredicate);
				}
				if(!possiblePathPredicates.isConstantFormula()) {
					this.nodeToAllPossiblePathPredicate.put(successor, possiblePathPredicates);
				}
			}
			this.processNode(successor, successorPathPredicate, formulaFactory);
		}
		this.visitedNodes.remove(node);
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
}
