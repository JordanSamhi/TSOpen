package com.github.dusby.tsopen.symbolicExecution;

import java.util.LinkedList;

import org.javatuples.Pair;

import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValueProvider;

import soot.Unit;

public class ContextualValues {
	
	LinkedList<Pair<Unit, SymbolicValueProvider>> values;
	
	public ContextualValues() {
		this.values = new LinkedList<Pair<Unit,SymbolicValueProvider>>();
	}

	public void addValue(Pair<Unit, SymbolicValueProvider> pair) {
		this.values.add(pair);
	}
	
	public SymbolicValueProvider getLastValue(){
		return this.values.getLast().getValue1();
	}
	
	public SymbolicValueProvider getValueByNode(Unit node) {
		for(Pair<Unit, SymbolicValueProvider> value : this.values) {
			if(value.getValue0() == node) {
				return value.getValue1();
			}
		}
		return null;
	}

}
