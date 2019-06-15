package com.github.dusby.tsopen.symbolicExecution;

import java.util.HashMap;
import java.util.Map;

import org.javatuples.Pair;

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
	private Map<Value, SymbolicValueProvider> symbolicExecutionResults;
	

	public SymbolicExecutioner(InfoflowCFG icfg, SootMethod mainMethod) {
		super(icfg, "Symbolic Execution", mainMethod);
		this.symbolicExecutionResults = new HashMap<Value, SymbolicValueProvider>();
	}

	public Map<Value, SymbolicValueProvider> getModelContext() {
		return this.symbolicExecutionResults;
	}

	/**
	 * Process the symbolic execution of the current node.
	 */
	@Override
	protected void processNodeBeforeNeighbors(Unit node) {
		RecognizerProcessor rp = null;
		Pair<Value, SymbolicValueProvider> results = null;
		DefinitionStmt defUnit = null;
		Value value0 = null;
		SymbolicValueProvider value1 = null;
		if(node instanceof DefinitionStmt) {
			defUnit = (DefinitionStmt) node;
			rp = new StringRecognizer(null, this);
			results = rp.recognize(defUnit);
			if(results != null) {
				value0 = results.getValue0();
				value1 = results.getValue1();
				if(value0 != null && value1 != null) {
					this.symbolicExecutionResults.put(value0, value1);
				}
			}
		}
	}
	
	@Override
	protected void processNeighbor(Unit node, Unit neighbour) {}
	
	@Override
	protected void processNodeAfterNeighbors(Unit node) {}
}
