package com.github.dusby.tsopen.symbolicExecution.symbolicValues;

import java.util.List;
import java.util.Map;

import com.github.dusby.tsopen.symbolicExecution.ContextualValues;
import com.github.dusby.tsopen.symbolicExecution.SymbolicExecutioner;

import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.Constant;

public class SymbolicValue implements SymbolicValueProvider {

	private Value base;
	private List<Value> args;
	private SootMethod method;
	private SymbolicExecutioner se;
	private String contextValue;
	private Unit node;

	public SymbolicValue(Value b, List<Value> a, SootMethod m, SymbolicExecutioner se, Unit node) {
		this.base = b;
		this.args = a;
		this.method = m;
		this.se = se;
		this.node = node;
		this.contextValue = this.computeValue();
	}

	private String computeValue() {
		String value = "(";
		if(this.base != null) {
			value += getValueFromModelContext(this.base);
			value += "->";
		}
		value += this.method.getName();
		value += "(";
		for(Value arg : this.args) {
			value += getValueFromModelContext(arg);
			if(arg != this.args.get(this.args.size() - 1)) {
				value += ", ";
			}
		}
		value += "))";
		return value;
	}

	private String getValueFromModelContext(Value v) {
		Map<Value, ContextualValues> context = this.se.getContext();
		if(context.containsKey(v)) {
			return context.get(v).getValueByNode(this.node).getContextValue();
		}else if(v instanceof Constant){
			return ((Constant)v).toString();
		}
		return v.getType().toString();
	}

	public List<Value> getArgs() {
		return this.args;
	}

	public Value getBase() {
		return this.base;
	}

	public SootMethod getMethod() {
		return this.method;
	}

	public String getContextValue() {
		return contextValue;
	}
}
