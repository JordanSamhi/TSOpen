package com.github.dusby.tsopen.symbolicExecution.symbolicValues;

import java.util.List;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecutioner;

import soot.Type;
import soot.Value;
import soot.tagkit.StringConstantValueTag;

public class ObjectValue extends ConcreteValue {

	private Type type;
	private List<Value> args;

	public ObjectValue(Type t, List<Value> args, SymbolicExecutioner se) {
		super(se);
		this.type = t;
		this.args = args;
	}

	@Override
	public String getValue() {
		String value = "";
		if(this.tags.isEmpty()) {
			return String.format("new %s(%s)", this.type, this.computeArgs());
		}
		for(StringConstantValueTag tag : this.tags) {
			value += tag.getStringValue();
		}
		return value;
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
