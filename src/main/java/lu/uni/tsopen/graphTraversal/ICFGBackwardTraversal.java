package lu.uni.tsopen.graphTraversal;

import java.util.Collection;
import java.util.List;

import soot.SootMethod;
import soot.Unit;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;

/**
 * Implementation of the backward ICFG traversal
 * @author Jordan Samhi
 *
 */
public abstract class ICFGBackwardTraversal extends ICFGTraversal {

	public ICFGBackwardTraversal(InfoflowCFG icfg, String nameOfAnalysis, SootMethod mainMethod) {
		super(icfg, nameOfAnalysis, mainMethod);
	}

	@Override
	public List<Unit> getNeighbors(Unit u) {
		return this.icfg.getPredsOf(u);
	}

	@Override
	public Collection<Unit> getExtremities(SootMethod m) {
		return this.icfg.getEndPointsOf(m);
	}

}
