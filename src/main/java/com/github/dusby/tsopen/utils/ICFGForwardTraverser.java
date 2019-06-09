package com.github.dusby.tsopen.utils;

import java.util.Collection;
import java.util.List;

import soot.SootMethod;
import soot.Unit;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;

/**
 * Implementation of the forward ICFG traversal
 * @author Jordan Samhi
 *
 */
public abstract class ICFGForwardTraverser extends ICFGTraverser {

	public ICFGForwardTraverser(InfoflowCFG icfg, String nameOfAnalysis, SootMethod mainMethod) {
		super(icfg, nameOfAnalysis, mainMethod);
	}

	@Override
	public List<Unit> getNeighbors(Unit u) {
		return this.icfg.getSuccsOf(u);
	}

	@Override
	public Collection<Unit> getExtremities(SootMethod m) {
		return this.icfg.getStartPointsOf(m);
	}

}
