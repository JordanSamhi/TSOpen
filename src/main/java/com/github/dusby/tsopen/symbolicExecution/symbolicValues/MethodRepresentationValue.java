package com.github.dusby.tsopen.symbolicExecution.symbolicValues;

import java.util.List;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;

import soot.SootMethod;
import soot.Value;
import soot.jimple.Constant;

public class MethodRepresentationValue extends AbstractSymbolicValue {

	private Value base;
	private List<Value> args;
	private SootMethod method;

	public MethodRepresentationValue(Value b, List<Value> a, SootMethod m, SymbolicExecution se) {
		super(se);
		this.method = m;
		this.base = b;
		this.args = a;

		if(this.base != null) {
			this.values.put(this.base, this.getSymbolicValues(this.base));
		}
		for(Value arg : this.args) {
			if(!(arg instanceof Constant)) {
				this.values.put(arg, this.getSymbolicValues(arg));
			}
		}
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

	public List<Value> getArgs() {
		return this.args;
	}
}
