package com.github.dusby.tsopen.utils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.profiler.Profiler;

import soot.SootMethod;
import soot.Unit;
import soot.jimple.DefinitionStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;

/**
 * Base class to traverse the ICFG backward or forward.
 * Implementing class would actually do the job on nodes.
 * @author Jordan Samhi
 *
 */
public abstract class ICFGTraverser {
	
	protected final String nameOfAnalysis;
	protected final InfoflowCFG icfg;
	private List<SootMethod> visitedMethods;
	private LinkedList<SootMethod> methodWorkList;
	private List<Unit> visitedNodes;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private Profiler profiler = new Profiler(this.getClass().getName());

	public ICFGTraverser(InfoflowCFG icfg, String nameOfAnalysis, SootMethod mainMethod) {
		this.nameOfAnalysis = nameOfAnalysis;
		this.icfg = icfg;
		this.visitedMethods = new LinkedList<SootMethod>();
		this.methodWorkList = new LinkedList<SootMethod>();
		this.visitedNodes = new LinkedList<Unit>();
		this.methodWorkList.add(mainMethod);
	}
	
	/**
	 * Begin the traversal of the ICFG as long as
	 * the method work-list is not empty.
	 */
	public void traverse() {
		profiler.start("traverse");
		SootMethod methodToAnalyze = null;
		Collection<Unit> extremities = null;
		while(!this.methodWorkList.isEmpty()) {
			methodToAnalyze = this.methodWorkList.removeFirst();
			this.visitedMethods.add(methodToAnalyze);
			extremities = this.getExtremities(methodToAnalyze);
			for(Unit extremity : extremities) {
				this.processNode(extremity);
			}
		}
		profiler.stop();
		this.logger.info("{} : {} ms", this.nameOfAnalysis, TimeUnit.MILLISECONDS.convert(profiler.elapsedTime(), TimeUnit.NANOSECONDS));
	}
	
	/**
	 * Propagate analysis on neighbors node and propagate
	 * discovered unvisited methods.
	 * @param node the basic block to process during analysis
	 */
	private void processNode(Unit node) {
		DefinitionStmt defUnit = null;
		if(!this.visitedNodes.contains(node)) {
			this.visitedNodes.add(node);
			if(node instanceof InvokeStmt) {
				this.propagateTargetMethod(node);
			}else if(node instanceof DefinitionStmt) {
				defUnit = (DefinitionStmt) node;
				if(defUnit.getRightOp() instanceof InvokeExpr) {
					this.propagateTargetMethod(defUnit);
				}
			}
			for(Unit neighbour : this.getNeighbors(node)) {
				this.processNeighbor(node, neighbour);
				this.processNode(neighbour);
			}
		}
	}
	
	/**
	 * Propagate the analysis on the points-to set
	 * of the invocation if methods have not yet been visited.
	 * Note that only methods of application classes are propagated.
	 * @param invocation the basic block representing a method invocation
	 */
	private void propagateTargetMethod(Unit invocation) {
		Collection<SootMethod> pointsTo = this.icfg.getCalleesOfCallAt(invocation);
		for(SootMethod callee : pointsTo) {
			if(callee.getDeclaringClass().isApplicationClass()) {
				if(!this.visitedMethods.contains(callee)) {
					this.visitedMethods.add(callee);
					this.methodWorkList.add(callee);
				}
			}
		}
	}
	
	/**
	 * Implementation depending on the kind of analysis.
	 * @param node the current node being analyzed
	 * @param neighbour the current neighbor of the node
	 */
	protected abstract void processNeighbor(Unit node, Unit neighbour);
	/**
	 * Returns predecessors or successors of the node depending
	 * on the kind of analysis (forward or backward)
	 * @param node
	 * @return a list of nodes
	 */
	protected abstract List<Unit> getNeighbors(Unit node);
	/**
	 * Return start-points or end-points depending on
	 * the kind of analysis (forward or backward)
	 * @param m
	 * @return a list of nodes
	 */
	protected abstract Collection<Unit> getExtremities(SootMethod m);
	
}