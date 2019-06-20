package com.github.dusby.tsopen.symbolicExecution;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;

import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import com.github.dusby.tsopen.symbolicExecution.typeRecognizers.DateRecognizer;
import com.github.dusby.tsopen.symbolicExecution.typeRecognizers.StringRecognizer;
import com.github.dusby.tsopen.symbolicExecution.typeRecognizers.TypeRecognizerProcessor;
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
	private TypeRecognizerProcessor trp;

	public SymbolicExecutioner(InfoflowCFG icfg, SootMethod mainMethod) {
		super(icfg, "Symbolic Execution", mainMethod);
		this.symbolicExecutionResults = new HashMap<Value, ContextualValues>();
		this.trp = new StringRecognizer(null, this, this.icfg);
		this.trp = new DateRecognizer(this.trp, this, this.icfg);
	}

	public Map<Value, ContextualValues> getContext() {
		return this.symbolicExecutionResults;
	}

	/**
	 * Process the symbolic execution of the current node.
	 */
	@Override
	protected void processNodeBeforeNeighbors(Unit node) {
		ContextualValues contextualValues = null;
		List<Pair<Value, SymbolicValue>> results = this.trp.recognize(node);
		Value value = null;
		SymbolicValue symbolicValue = null;

		if(results != null) {
			for(Pair<Value, SymbolicValue> p : results) {
				value = p.getValue0();
				symbolicValue = p.getValue1();
				contextualValues = this.symbolicExecutionResults.get(value);
				if(contextualValues == null) {
					contextualValues = new ContextualValues(this);
					this.symbolicExecutionResults.put(value, contextualValues);
				}
				contextualValues.addValue(node, symbolicValue);
			}
		}
	}

	@Override
	protected void processNeighbor(Unit node, Unit neighbour) {}

	@Override
	protected void processNodeAfterNeighbors(Unit node) {}
}
