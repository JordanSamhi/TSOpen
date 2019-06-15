package com.github.dusby.tsopen.symbolicExecution;

import java.util.HashMap;
import java.util.Map;

import org.javatuples.Pair;
import org.logicng.formulas.Formula;

import com.github.dusby.tsopen.pathPredicateRecovery.PathPredicateRecoverer;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValueProvider;
import com.github.dusby.tsopen.symbolicExecution.typeRecognizers.RecognizerProcessor;
import com.github.dusby.tsopen.symbolicExecution.typeRecognizers.StringRecognizer;
import com.github.dusby.tsopen.utils.ICFGForwardTraverser;

import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;

/**
 * 
 * This is the class implementing the symbolic execution
 * @author Jordan Samhi
 *
 */
public class SymbolicExecutioner extends ICFGForwardTraverser {
	private Map<Value, Pair<Formula, SymbolicValueProvider>> symbolicExecutionResults;
	private PathPredicateRecoverer ppr;

	public SymbolicExecutioner(InfoflowCFG icfg, SootMethod mainMethod, PathPredicateRecoverer ppr) {
		super(icfg, "Symbolic Execution", mainMethod);
		this.symbolicExecutionResults = new HashMap<Value, Pair<Formula, SymbolicValueProvider>>();
		this.ppr = ppr;
	}

	public Map<Value, Pair<Formula, SymbolicValueProvider>> getModelContext() {
		return this.symbolicExecutionResults;
	}

	/**
	 * Process the symbolic execution of the current node.
	 */
	@Override
	protected void processNodeBeforeNeighbors(Unit node) {
		RecognizerProcessor rp = null;
		SymbolicValueProvider results = null;
		DefinitionStmt defUnit = null;
		Value leftOp = null,
			  rightOp = null;
		Formula nodeToFullPath = this.ppr.getNodeFullPath(node);
		if(node instanceof DefinitionStmt) {
			defUnit = (DefinitionStmt) node;
			leftOp = defUnit.getLeftOp();
			rightOp = defUnit.getRightOp();
			rp = new StringRecognizer(null, this);
			results = rp.recognize(leftOp, rightOp);
			if(results != null) {
				this.symbolicExecutionResults.put(leftOp, new Pair<Formula, SymbolicValueProvider>(nodeToFullPath, results));
			}
		}
	}

	@Override
	protected void processNeighbor(Unit node, Unit neighbour) {}

	@Override
	protected void processNodeAfterNeighbors(Unit node) {}
}
