package com.github.dusby.tsopen.pathPredicateRecovery;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecutioner;
import com.github.dusby.tsopen.utils.Edge;
import com.github.dusby.tsopen.utils.ICFGBackwardTraverser;

import soot.SootMethod;
import soot.Unit;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;

public class PathPredicateRecoverer extends ICFGBackwardTraverser {
	private final SymbolicExecutioner se;
	
	public PathPredicateRecoverer(InfoflowCFG icfg, SymbolicExecutioner se, SootMethod mainMethod) {
		super(icfg, "Path Predicate Recovery", mainMethod);
		this.se = se;
	}

	@Override
	protected void processNeighbor(Unit node, Unit neighbour) {
		Edge edge = se.getAnnotatedEdge(neighbour, node);
	}
}
