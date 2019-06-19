package com.github.dusby.tsopen.symbolicExecution;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValueProvider;

import soot.Unit;

public class ContextualValues {

	private LinkedHashMap<Unit, LinkedList<SymbolicValueProvider>> values;
	private SymbolicExecutioner se;

	public ContextualValues(SymbolicExecutioner se) {
		this.values = new LinkedHashMap<Unit, LinkedList<SymbolicValueProvider>>();
		this.se = se;
	}

	public void addValue(Unit node, SymbolicValueProvider svp) {
		LinkedList<SymbolicValueProvider> valuesOfNode = this.values.get(node);
		if(valuesOfNode == null) {
			valuesOfNode = new LinkedList<SymbolicValueProvider>();
			this.values.put(node, valuesOfNode);
		}
		valuesOfNode.add(svp);
	}

	/**
	 * Return last available values on the current path if possible.
	 * Otherwise the last computed values
	 * @return a list of symbolic values
	 */
	public List<SymbolicValueProvider> getLastCoherentValues(){
		Iterator<Unit> it = this.se.getCurrentPath().descendingIterator();
		Unit node = null;
		LinkedList<SymbolicValueProvider> lasts = null;
		LinkedList<SymbolicValueProvider> values = null;
		while(it.hasNext()) {
			node = it.next();
			values = this.values.get(node);
			if(values != null) {
				return values;
			}
		}
		for(Entry<Unit, LinkedList<SymbolicValueProvider>> e : this.values.entrySet()) {
			lasts = e.getValue();
		}
		return lasts;
	}
}
