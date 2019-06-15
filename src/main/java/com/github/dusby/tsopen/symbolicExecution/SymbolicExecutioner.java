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
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;

/**
 * 
 * This is the class implementing the symbolic execution
 * @author Jordan Samhi
 *
 */
public class SymbolicExecutioner extends ICFGForwardTraverser {
	private Map<Value, ContextualValues> symbolicExecutionResults;
	private RecognizerProcessor rp;

	public SymbolicExecutioner(InfoflowCFG icfg, SootMethod mainMethod) {
		super(icfg, "Symbolic Execution", mainMethod);
		this.symbolicExecutionResults = new HashMap<Value, ContextualValues>();
		this.rp = new StringRecognizer(null, this, this.icfg);
	}

	public Map<Value, ContextualValues> getContext() {
		return this.symbolicExecutionResults;
	}

	/**
	 * Process the symbolic execution of the current node.
	 */
	@Override
	protected void processNodeBeforeNeighbors(Unit node) {
		Pair<Value, SymbolicValueProvider> valueToSymbolicValue = null;
		ContextualValues contextualValues = null;
		valueToSymbolicValue = this.rp.recognize(node);
		Value value = null;
		SymbolicValueProvider symbolicValue = null;
		if(valueToSymbolicValue != null) {
			value = valueToSymbolicValue.getValue0();
			symbolicValue = valueToSymbolicValue.getValue1();
			contextualValues = this.symbolicExecutionResults.get(value);
			if(contextualValues == null) {
				contextualValues = new ContextualValues();
			}
			contextualValues.addValue(node, symbolicValue);
			this.symbolicExecutionResults.put(value, contextualValues);
		}
	}

	@Override
	protected void processNeighbor(Unit node, Unit neighbour) {}

	@Override
	protected void processNodeAfterNeighbors(Unit node) {}
}
