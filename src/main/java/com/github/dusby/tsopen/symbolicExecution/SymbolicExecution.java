package com.github.dusby.tsopen.symbolicExecution;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;

import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import com.github.dusby.tsopen.symbolicExecution.typeRecognition.DateTimeRecognition;
import com.github.dusby.tsopen.symbolicExecution.typeRecognition.IntRecognition;
import com.github.dusby.tsopen.symbolicExecution.typeRecognition.LocationRecognition;
import com.github.dusby.tsopen.symbolicExecution.typeRecognition.LongRecognition;
import com.github.dusby.tsopen.symbolicExecution.typeRecognition.SmsRecognition;
import com.github.dusby.tsopen.symbolicExecution.typeRecognition.StringRecognition;
import com.github.dusby.tsopen.symbolicExecution.typeRecognition.TypeRecognitionHandler;
import com.github.dusby.tsopen.utils.ICFGForwardTraversal;

import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;

/**
 *
 * Model int, String, Location, SMS and Time related locals and fields
 * @author Jordan Samhi
 *
 */
public class SymbolicExecution extends ICFGForwardTraversal {
	private Map<Value, ContextualValues> symbolicExecutionResults;
	private TypeRecognitionHandler trh;

	public SymbolicExecution(InfoflowCFG icfg, SootMethod mainMethod) {
		super(icfg, "Symbolic Execution", mainMethod);
		this.symbolicExecutionResults = new HashMap<Value, ContextualValues>();
		this.trh = new StringRecognition(null, this, this.icfg);
		this.trh = new DateTimeRecognition(this.trh, this, this.icfg);
		this.trh = new LocationRecognition(this.trh, this, this.icfg);
		this.trh = new SmsRecognition(this.trh, this, this.icfg);
		this.trh = new LongRecognition(this.trh, this, this.icfg);
		this.trh = new IntRecognition(this.trh, this, this.icfg);
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
		List<Pair<Value, SymbolicValue>> results = this.trh.recognizeType(node);
		Value value = null;
		SymbolicValue symbolicValue = null;

		if(results != null) {
			for(Pair<Value, SymbolicValue> p : results) {
				value = p.getValue0();
				symbolicValue = p.getValue1();
				if(symbolicValue.hasTag()) {
					this.logger.debug("{}", symbolicValue);
				}
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
