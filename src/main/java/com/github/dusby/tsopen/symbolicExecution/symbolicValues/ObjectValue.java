package com.github.dusby.tsopen.symbolicExecution.symbolicValues;

import java.util.List;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;

import soot.Type;
import soot.Value;

public class ObjectValue extends ConcreteValue {

	private Type type;
	private List<Value> args;

	public ObjectValue(Type t, List<Value> args, SymbolicExecution se) {
		super(se);
		this.type = t;
		this.args = args;
	}

	@Override
	public String getValue() {
		return String.format("%s(%s)", this.type, this.computeArgs());
	}

	private String computeArgs() {
		String args = "";
		List<SymbolicValue> values = null;
		for(Value arg : this.args) {
			values = this.getSymbolicValues(arg);
			if(values != null) {
				for(SymbolicValue sv : values) {
					args += sv;
					if(sv != values.get(values.size() - 1)) {
						args += " | ";
					}
				}
			}else {
				args += arg.getType();
			}
			if(arg != this.args.get(this.args.size() - 1)) {
				args += ", ";
			}
		}
		return args;
	}

	@Override
	public boolean isConstant() {
		return false;
	}

	@Override
	public boolean isObject() {
		return true;
	}
}
