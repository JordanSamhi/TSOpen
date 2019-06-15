package com.github.dusby.tsopen.pathPredicateRecovery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.transformations.DistributiveSimplifier;

import com.github.dusby.tsopen.utils.Edge;
import com.github.dusby.tsopen.utils.ICFGBackwardTraverser;
import com.github.dusby.tsopen.utils.Utils;

import soot.SootMethod;
import soot.Unit;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;

public class PathPredicateRecoverer extends ICFGBackwardTraverser {

	private final SimpleBlockPredicateExtractioner sbpe;
	private Map<Unit, List<Formula>> nodeToPathPredicates;
	private Map<Unit, Formula> nodeToFullPathPredicate;
	private final FormulaFactory formulaFactory;
	private final DistributiveSimplifier simplifier;
	private final boolean handleExceptions;
	
	public PathPredicateRecoverer(InfoflowCFG icfg, SimpleBlockPredicateExtractioner sbpe, SootMethod mainMethod, boolean handleExceptions) {
		super(icfg, "Path Predicate Recovery", mainMethod);
		this.sbpe = sbpe;
		this.nodeToPathPredicates = new HashMap<Unit, List<Formula>>();
		this.nodeToFullPathPredicate = new HashMap<Unit, Formula>();
		this.formulaFactory = new FormulaFactory();
		this.simplifier = new DistributiveSimplifier();
		this.handleExceptions = handleExceptions;
	}

	@Override
	protected void processNeighbor(Unit node, Unit neighbour) {
		if(this.handleExceptions) {
			this.annotateNodeWithPathPredicate(node, neighbour);
		}else if(!Utils.isCaughtException(node)) {
			this.annotateNodeWithPathPredicate(node, neighbour);
		}
	}

	private void annotateNodeWithPathPredicate(Unit node, Unit neighbour) {
		Edge edge = sbpe.getAnnotatedEdge(neighbour, node);
		Formula currentPathPredicate = null,
				neighborPathPredicate = this.nodeToFullPathPredicate.get(neighbour);
		List<Formula> nodePredicates = this.nodeToPathPredicates.get(node);
		if(edge != null) {
			if(neighborPathPredicate != null) {
				currentPathPredicate = this.formulaFactory.and(edge.getPredicate(), neighborPathPredicate);
			}else {
				currentPathPredicate = edge.getPredicate();
			}
		}else {
			currentPathPredicate = neighborPathPredicate;
		}
		if(currentPathPredicate != null) {
			if(nodePredicates == null) {
				nodePredicates = new ArrayList<Formula>();
				this.nodeToPathPredicates.put(node, nodePredicates);
			}
			nodePredicates.add(currentPathPredicate);
		}
	}

	public Map<Unit, Formula> getNodeToFullPathPredicate() {
		return nodeToFullPathPredicate;
	}

	@Override
	protected void processNodeAfterNeighbors(Unit node) {
		List<Formula> nodePredicates = this.nodeToPathPredicates.get(node);
		Formula simplifiedPredicate = null;
		if(nodePredicates != null) {
			simplifiedPredicate = this.simplifier.apply(this.formulaFactory.or(nodePredicates), true);
			this.nodeToFullPathPredicate.put(node, simplifiedPredicate);
		}
	}

	@Override
	protected void processNodeBeforeNeighbors(Unit node) {}
}
