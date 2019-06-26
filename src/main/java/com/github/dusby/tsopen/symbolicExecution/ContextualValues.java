package com.github.dusby.tsopen.symbolicExecution;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;

import soot.Unit;

public class ContextualValues {

	private LinkedHashMap<Unit, LinkedList<SymbolicValue>> values;
	private SymbolicExecution se;

	public ContextualValues(SymbolicExecution se) {
		this.values = new LinkedHashMap<Unit, LinkedList<SymbolicValue>>();
		this.se = se;
	}

	public void addValue(Unit node, SymbolicValue sv) {
		LinkedList<SymbolicValue> valuesOfNode = this.values.get(node);
		if(valuesOfNode == null) {
			valuesOfNode = new LinkedList<SymbolicValue>();
			this.values.put(node, valuesOfNode);
		}
		valuesOfNode.add(sv);
	}

	/**
	 * Return last available values on the current path if possible.
	 * Otherwise the last computed values
	 * @return a list of symbolic values
	 */
	public List<SymbolicValue> getLastCoherentValues() {
		Iterator<Unit> it = this.se.getCurrentPath().descendingIterator();
		Unit node = null;
		LinkedList<SymbolicValue> lasts = null;
		LinkedList<SymbolicValue> values = null;
		while(it.hasNext()) {
			node = it.next();
			values = this.values.get(node);
			if(values != null) {
				return values;
			}
		}
		for(Entry<Unit, LinkedList<SymbolicValue>> e : this.values.entrySet()) {
			lasts = e.getValue();
		}
		return lasts;
	}

	public List<SymbolicValue> getAllValues() {
		List<SymbolicValue> values = new ArrayList<SymbolicValue>();
		for(Entry<Unit, LinkedList<SymbolicValue>> e : this.values.entrySet()) {
			values.addAll(e.getValue());
		}
		return values;
	}
}
