package com.github.JordanSamhi.tsopen.symbolicExecution;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.github.JordanSamhi.tsopen.symbolicExecution.symbolicValues.SymbolicValue;

import soot.Unit;
import soot.Value;

public class ContextualValues {

	private Value receiver;
	private LinkedHashMap<Unit, LinkedList<SymbolicValue>> nodesToSymbolicValues;
	private SymbolicExecution se;

	public ContextualValues(SymbolicExecution se, Value receiver) {
		this.nodesToSymbolicValues = new LinkedHashMap<Unit, LinkedList<SymbolicValue>>();
		this.se = se;
		this.receiver = receiver;
	}

	public void addValue(Unit node, SymbolicValue sv) {
		LinkedList<SymbolicValue> valuesOfNode = this.nodesToSymbolicValues.get(node);
		if(valuesOfNode == null) {
			valuesOfNode = new LinkedList<SymbolicValue>();
			this.nodesToSymbolicValues.put(node, valuesOfNode);
		}
		valuesOfNode.add(sv);
	}

	/**
	 * Return last available values on the current path if possible.
	 * Otherwise the last computed values
	 * @return a list of symbolic values
	 */
	public List<SymbolicValue> getLastCoherentValues(Unit node) {
		Iterator<Unit> it = this.se.getCurrentPath().descendingIterator();
		LinkedList<SymbolicValue> values = null;
		Unit n = null;
		Map<Value, List<SymbolicValue>> valuesAtNode = null;
		if(node == null) {
			while(it.hasNext()) {
				n = it.next();
				if(n != this.se.getCurrentPath().getLast()) {
					values = this.nodesToSymbolicValues.get(n);
					if(values != null) {
						return values;
					}
				}
			}
		}else {
			valuesAtNode = this.se.getValuesAtNode(node);
			if(valuesAtNode != null) {
				return valuesAtNode.get(this.receiver);
			}
		}
		return this.getAllValues();
	}

	public List<SymbolicValue> getAllValues() {
		List<SymbolicValue> values = new ArrayList<SymbolicValue>();
		for(Entry<Unit, LinkedList<SymbolicValue>> e : this.nodesToSymbolicValues.entrySet()) {
			values.addAll(e.getValue());
		}
		return values;
	}
}
