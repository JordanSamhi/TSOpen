package com.github.dusby.tsopen.symbolicExecution.symbolicValues;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.dusby.tsopen.symbolicExecution.ContextualValues;
import com.github.dusby.tsopen.symbolicExecution.SymbolicExecutioner;

import soot.SootMethod;
import soot.Value;
import soot.jimple.Constant;

public class MethodRepresentationValue implements SymbolicValueProvider {

	private Map<Value, List<SymbolicValueProvider>> values;
	private Value base;
	private List<Value> args;
	private SootMethod method;
	private SymbolicExecutioner se;

	public MethodRepresentationValue(Value b, List<Value> a, SootMethod m, SymbolicExecutioner se) {
		this.values = new HashMap<Value, List<SymbolicValueProvider>>();
		this.method = m;
		this.se = se;
		this.base = b;
		this.args = a;

		this.populateValues(this.base);
		for(Value arg : this.args) {
			this.populateValues(arg);
		}
	}

	private void populateValues(Value v) {
		Map<Value, ContextualValues> context = this.se.getContext();
		List<SymbolicValueProvider> values = null;
		if(context.containsKey(v)) {
			values = context.get(v).getLastCoherentValues();
			this.values.put(v, values);
		}
	}

	private String computeValue(Value v) {
		List<SymbolicValueProvider> values = null;
		String s = "";
		if(this.values.containsKey(v)) {
			values = this.values.get(v);
			for(SymbolicValueProvider svp : values) {
				s += svp;
				if(svp != values.get(values.size() - 1)) {
					s += " | ";
				}
			}
			return s;
		}else if(v instanceof Constant){
			return ((Constant)v).toString();
		}
		return v.getType().toString();
	}

	@Override
	public String getValue() {
		String value = "(";
		if(this.base != null) {
			value += this.computeValue(this.base);
			value += ".";
		}
		value += this.method.getName();
		value += "(";
		for(Value arg : this.args) {
			value += this.computeValue(arg);
		}
		value += "))";
		return value;
	}

	@Override
	public String toString() {
		return this.getValue();
	}

	@Override
	public boolean isSymbolic() {
		return false;
	}

	@Override
	public boolean isConcrete() {
		return false;
	}

	@Override
	public boolean isMethodRepresentation() {
		return true;
	}
}
