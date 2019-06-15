package com.github.dusby.tsopen.symbolicExecution;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValueProvider;

import soot.Unit;

public class ContextualValues {
	
	LinkedHashMap<Unit, LinkedList<SymbolicValueProvider>> values;
	
	public ContextualValues() {
		this.values = new LinkedHashMap<Unit, LinkedList<SymbolicValueProvider>>();
	}

	public void addValue(Unit node, SymbolicValueProvider svp) {
		LinkedList<SymbolicValueProvider> valuesOfNode = this.values.get(node);
		if(valuesOfNode == null) {
			valuesOfNode = new LinkedList<SymbolicValueProvider>();
			this.values.put(node, valuesOfNode);
		}
		valuesOfNode.add(svp);
	}
	
	public SymbolicValueProvider getLastValue(){
		LinkedList<SymbolicValueProvider> last = null;
		for(Entry<Unit, LinkedList<SymbolicValueProvider>> e : this.values.entrySet()) {
			last = e.getValue();
		}
		return last.getLast();
	}
	
	public List<SymbolicValueProvider> getValuesByNode(Unit node) {
		return this.values.get(node);
	}
}
