package com.github.dusby.tsopen.symbolicExecution.symbolicValues;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;

import soot.SootMethod;
import soot.Value;
import soot.jimple.Constant;

public class MethodRepresentationValue extends AbstractSymbolicValue {

	private Map<Value, List<SymbolicValue>> values;
	private Value base;
	private List<Value> args;
	private SootMethod method;

	public MethodRepresentationValue(Value b, List<Value> a, SootMethod m, SymbolicExecution se) {
		super(se);
		this.values = new HashMap<Value, List<SymbolicValue>>();
		this.method = m;
		this.base = b;
		this.args = a;

		if(this.base != null) {
			this.values.put(this.base, this.getSymbolicValues(this.base));
		}
		for(Value arg : this.args) {
			this.values.put(arg, this.getSymbolicValues(arg));
		}
	}

	private String computeValue(Value v) {
		List<SymbolicValue> values = null;
		String s = "";
		values = this.values.get(v);
		if(values != null) {
			for(SymbolicValue sv : values) {
				s += sv;
				if(sv != values.get(values.size() - 1)) {
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
			if(arg != this.args.get(this.args.size() - 1)) {
				value += ", ";
			}
		}
		value += "))";
		return value;
	}

	@Override
	public boolean isSymbolic() {
		return false;
	}

	@Override
	public boolean isConstant() {
		return false;
	}

	@Override
	public boolean isMethodRepresentation() {
		return true;
	}

	@Override
	public boolean isObject() {
		return false;
	}

	@Override
	public Value getBase() {
		return this.base;
	}
}
