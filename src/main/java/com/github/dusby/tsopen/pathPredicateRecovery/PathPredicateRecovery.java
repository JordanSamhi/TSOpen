package com.github.dusby.tsopen.pathPredicateRecovery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Literal;
import org.logicng.transformations.DistributiveSimplifier;

import com.github.dusby.tsopen.utils.Edge;
import com.github.dusby.tsopen.utils.ICFGBackwardTraversal;
import com.github.dusby.tsopen.utils.Utils;

import soot.SootMethod;
import soot.Unit;
import soot.jimple.IfStmt;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;

public class PathPredicateRecovery extends ICFGBackwardTraversal {

	private final SimpleBlockPredicateExtraction sbpe;
	private Map<Unit, List<Formula>> nodeToPathPredicates;
	private Map<Unit, Formula> nodeToFullPathPredicate;
	private final FormulaFactory formulaFactory;
	private final DistributiveSimplifier simplifier;
	private final boolean handleExceptions;
	private Map<IfStmt, List<Unit>> guardedBlocks;

	public PathPredicateRecovery(InfoflowCFG icfg, SimpleBlockPredicateExtraction sbpe, SootMethod mainMethod, boolean handleExceptions) {
		super(icfg, "Path Predicate Recovery", mainMethod);
		this.sbpe = sbpe;
		this.nodeToPathPredicates = new HashMap<Unit, List<Formula>>();
		this.nodeToFullPathPredicate = new HashMap<Unit, Formula>();
		this.formulaFactory = new FormulaFactory();
		this.simplifier = new DistributiveSimplifier();
		this.handleExceptions = handleExceptions;
		this.guardedBlocks = new HashMap<IfStmt, List<Unit>>();
	}

	private void annotateNodeWithPathPredicate(Unit node, Unit neighbour) {
		Edge edge = this.sbpe.getAnnotatedEdge(neighbour, node);
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

	public Formula getNodeFullPath(Unit node) {
		if(this.nodeToFullPathPredicate.containsKey(node)) {
			return this.nodeToFullPathPredicate.get(node);
		}
		return null;
	}

	@Override
	protected void processNeighbor(Unit node, Unit neighbour) {
		if(this.handleExceptions || !Utils.isCaughtException(node)) {
			this.annotateNodeWithPathPredicate(node, neighbour);
		}
	}

	/**
	 * Merge path predicates with logical OR and simplify the formula
	 */
	@Override
	protected void processNodeAfterNeighbors(Unit node) {
		List<Formula> nodePredicates = this.nodeToPathPredicates.get(node);
		Formula simplifiedPredicate = null;
		if(nodePredicates != null) {
			simplifiedPredicate = this.simplifier.apply(this.formulaFactory.or(nodePredicates), true);
			this.nodeToFullPathPredicate.put(node, simplifiedPredicate);
			if(!(node instanceof IfStmt)) {
				this.computeGuardedBlocks(node);
			}
		}
	}

	private void computeGuardedBlocks(Unit node) {
		Formula fullPath = this.getNodeFullPath(node);
		IfStmt ifStmt = null;
		List<Unit> blocks = null;
		if(fullPath != null) {
			for(Literal lit : fullPath.literals()) {
				ifStmt = this.sbpe.getCondtionFromLiteral(lit);
				blocks = this.guardedBlocks.get(ifStmt);
				if(ifStmt != null) {
					if(blocks == null) {
						blocks = new ArrayList<Unit>();
						this.guardedBlocks.put(ifStmt, blocks);
					}
					if(!blocks.contains(node)) {
						blocks.add(node);
					}
				}
			}
		}
	}

	public List<Unit> getGuardedBlocks(IfStmt ifStmt){
		if(this.guardedBlocks.containsKey(ifStmt)) {
			return this.guardedBlocks.get(ifStmt);
		}
		return null;
	}

	@Override
	protected void processNodeBeforeNeighbors(Unit node) {}
}
