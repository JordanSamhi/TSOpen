package com.github.JordanSamhi.tsopen.symbolicExecution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.javatuples.Pair;

import com.github.JordanSamhi.tsopen.graphTraversal.ICFGForwardTraversal;
import com.github.JordanSamhi.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import com.github.JordanSamhi.tsopen.symbolicExecution.typeRecognition.BooleanRecognition;
import com.github.JordanSamhi.tsopen.symbolicExecution.typeRecognition.ByteRecognition;
import com.github.JordanSamhi.tsopen.symbolicExecution.typeRecognition.DateTimeRecognition;
import com.github.JordanSamhi.tsopen.symbolicExecution.typeRecognition.DoubleRecognition;
import com.github.JordanSamhi.tsopen.symbolicExecution.typeRecognition.FloatArrayRecognition;
import com.github.JordanSamhi.tsopen.symbolicExecution.typeRecognition.FloatRecognition;
import com.github.JordanSamhi.tsopen.symbolicExecution.typeRecognition.IntRecognition;
import com.github.JordanSamhi.tsopen.symbolicExecution.typeRecognition.LocationRecognition;
import com.github.JordanSamhi.tsopen.symbolicExecution.typeRecognition.LongRecognition;
import com.github.JordanSamhi.tsopen.symbolicExecution.typeRecognition.SmsRecognition;
import com.github.JordanSamhi.tsopen.symbolicExecution.typeRecognition.StringRecognition;
import com.github.JordanSamhi.tsopen.symbolicExecution.typeRecognition.TypeRecognitionHandler;

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
	private Map<Unit, Map<Value, List<SymbolicValue>>> valuesAtNode;
	private Map<Value, List<SymbolicValue>> currentValues;
	private TypeRecognitionHandler trh;

	public SymbolicExecution(InfoflowCFG icfg, SootMethod mainMethod) {
		super(icfg, "Symbolic Execution", mainMethod);
		this.symbolicExecutionResults = new HashMap<Value, ContextualValues>();
		this.valuesAtNode = new HashMap<Unit, Map<Value,List<SymbolicValue>>>();
		this.currentValues = new HashMap<Value, List<SymbolicValue>>();
		this.trh = new StringRecognition(null, this, this.icfg);
		this.trh = new DateTimeRecognition(this.trh, this, this.icfg);
		this.trh = new LocationRecognition(this.trh, this, this.icfg);
		this.trh = new SmsRecognition(this.trh, this, this.icfg);
		this.trh = new LongRecognition(this.trh, this, this.icfg);
		this.trh = new IntRecognition(this.trh, this, this.icfg);
		this.trh = new BooleanRecognition(this.trh, this, icfg);
		this.trh = new ByteRecognition(this.trh, this, icfg);
		this.trh = new DoubleRecognition(this.trh, this, icfg);
		this.trh = new FloatArrayRecognition(this.trh, this, icfg);
		this.trh = new FloatRecognition(this.trh, this, icfg);
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
		this.updateValuesAtNode(results, node);
		if(results != null) {
			for(Pair<Value, SymbolicValue> p : results) {
				value = p.getValue0();
				symbolicValue = p.getValue1();
				contextualValues = this.symbolicExecutionResults.get(value);
				if(contextualValues == null) {
					contextualValues = new ContextualValues(this, value);
					if(value != null) {
						this.symbolicExecutionResults.put(value, contextualValues);
					}
				}
				contextualValues.addValue(node, symbolicValue);
			}
		}
	}

	private void updateValuesAtNode(List<Pair<Value, SymbolicValue>> results, Unit node) {
		Value value = null;
		SymbolicValue symbolicValue = null;
		List<SymbolicValue> symValues = null;
		Map<Value, List<SymbolicValue>> currentSymValues = new HashMap<Value, List<SymbolicValue>>();
		Map<Value, List<SymbolicValue>> tmpSymValues = new HashMap<Value, List<SymbolicValue>>();
		currentSymValues.putAll(this.currentValues);
		if(results != null) {
			for(Pair<Value, SymbolicValue> p : results) {
				value = p.getValue0();
				symbolicValue = p.getValue1();
				symValues = tmpSymValues.get(value);
				if(symValues == null) {
					symValues = new ArrayList<SymbolicValue>();
					tmpSymValues.put(value, symValues);
				}
				symValues.add(symbolicValue);
			}
			for(Entry<Value, List<SymbolicValue>> e : tmpSymValues.entrySet()) {
				currentSymValues.put(e.getKey(), e.getValue());
			}
		}
		this.valuesAtNode.put(node, currentSymValues);
		this.currentValues = currentSymValues;
	}

	public Map<Value, ContextualValues> getContext() {
		return this.symbolicExecutionResults;
	}

	public ContextualValues getContextualValues(Value v) {
		if(this.symbolicExecutionResults.containsKey(v)) {
			return this.symbolicExecutionResults.get(v);
		}
		return null;
	}

	public Map<Value, List<SymbolicValue>> getValuesAtNode(Unit node) {
		return this.valuesAtNode.get(node);
	}

	@Override
	protected void processNeighbor(Unit node, Unit neighbour) {}

	@Override
	protected void processNodeAfterNeighbors(Unit node) {}
}
