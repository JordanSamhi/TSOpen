package com.github.dusby.tsopen.symbolicExecution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Literal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.profiler.Profiler;

import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValueProvider;
import com.github.dusby.tsopen.utils.Edge;

import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.ConditionExpr;
import soot.jimple.DefinitionStmt;
import soot.jimple.IfStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;
import soot.jimple.internal.IdentityRefBox;

public class SymbolicExecutioner {
	private final InfoflowCFG icfg;
	private List<Unit> visitedNodes;
	private List<SootMethod> visitedMethods;
	private Map<Value, SymbolicValueProvider> symbolicExecutionResults;
	private LinkedList<SootMethod> methodWorkList;
	private Map<Edge, Literal> simpleBlockPredicates;
	private Map<Literal, ConditionExpr> literalToCondition = null;
	private Map<Unit, List<Edge>> nodeToEdges;
	private final FormulaFactory formulaFactory;

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private Profiler profiler = new Profiler(this.getClass().getName());

	public SymbolicExecutioner(InfoflowCFG icfg, SootMethod mainMethod) {
		this.icfg = icfg;
		this.visitedNodes = new ArrayList<Unit>();
		this.visitedMethods = new ArrayList<SootMethod>();
		this.symbolicExecutionResults = new HashMap<Value, SymbolicValueProvider>();
		this.methodWorkList = new LinkedList<SootMethod>();
		this.simpleBlockPredicates = new HashMap<Edge, Literal>();
		this.literalToCondition = new HashMap<Literal, ConditionExpr>();
		this.nodeToEdges = new HashMap<Unit, List<Edge>>();
		this.formulaFactory = new FormulaFactory();
		this.methodWorkList.add(mainMethod);
	}

	public void execute() {
		profiler.start("execute");
		SootMethod methodToAnalyze = null;
		Unit entryPoint = null;
		while(!this.methodWorkList.isEmpty()) {
			methodToAnalyze = this.methodWorkList.removeFirst();
			this.visitedMethods.add(methodToAnalyze);
			entryPoint = this.icfg.getStartPointsOf(methodToAnalyze).iterator().next();
			this.processNode(entryPoint);
		}
		profiler.stop();
		this.logger.info("Symbolic execution : {} ms", TimeUnit.MILLISECONDS.convert(profiler.elapsedTime(), TimeUnit.NANOSECONDS));
	}

	private void processNode(Unit node) {
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
			//			this.annotateEdgeWithPathPredicate(node, formulaFactory);
			for(Unit successor : this.icfg.getSuccsOf(node)) {
				this.predicateAnnotation(node, successor);
				this.processNode(successor);
			}
		}
	}

	private void predicateAnnotation(Unit node, Unit successor) {
		IfStmt ifStmt = null;
		String condition = null;
		Edge edge = null;
		Literal predicateToPropagate = null;
		List<Edge> edgesOfNode = this.nodeToEdges.get(node);

		if(!this.isCaughtException(successor)) {
			if(node instanceof IfStmt) {
				if(edgesOfNode == null) {
					edgesOfNode = new ArrayList<Edge>();
				}
				edge = new Edge(node, successor);
				edgesOfNode.add(edge);
				this.nodeToEdges.put(node, edgesOfNode);
				ifStmt = (IfStmt) node;
				condition = String.format("([%s] => %s)", ifStmt.hashCode(), ifStmt.getCondition().toString());
				if(successor == ifStmt.getTarget()) {
					predicateToPropagate = this.formulaFactory.literal(condition, true);
				}else {
					predicateToPropagate = this.formulaFactory.literal(condition, false);
				}
				this.literalToCondition.put(predicateToPropagate, (ConditionExpr) ifStmt.getCondition());
				this.simpleBlockPredicates.put(edge, predicateToPropagate);
			}
		}
	}

	private boolean isCaughtException(Unit u) {
		for(ValueBox useBox : u.getUseBoxes()) {
			if(useBox instanceof IdentityRefBox) {
				if(((IdentityRefBox) useBox).getValue() instanceof CaughtExceptionRef) {
					return true;
				}
			}
		}
		return false;
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

	public Map<Value, SymbolicValueProvider> getModelContext() {
		return this.symbolicExecutionResults;
	}
}
