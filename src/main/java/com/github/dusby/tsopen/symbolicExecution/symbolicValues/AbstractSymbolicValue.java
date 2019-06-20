package com.github.dusby.tsopen.symbolicExecution.symbolicValues;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.dusby.tsopen.symbolicExecution.ContextualValues;
import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;

import soot.Value;
import soot.tagkit.StringConstantValueTag;

public abstract class AbstractSymbolicValue implements SymbolicValue {

	protected List<StringConstantValueTag> tags;
	protected SymbolicExecution se;

	public AbstractSymbolicValue(SymbolicExecution se) {
		this.tags = new ArrayList<StringConstantValueTag>();
		this.se = se;
	}

	public AbstractSymbolicValue() {
		this.tags = new ArrayList<StringConstantValueTag>();
	}

	protected List<SymbolicValue> getSymbolicValues(Value v) {
		Map<Value, ContextualValues> context = this.se.getContext();
		if(context.containsKey(v)) {
			return context.get(v).getLastCoherentValues();
		}
		return null;
	}

	@Override
	public List<StringConstantValueTag> getTags() {
		return this.tags;
	}

	@Override
	public String getStringTags() {
		String tags = "";
		for(StringConstantValueTag scvt : this.tags) {
			tags += String.format("%s ", scvt.getStringValue());
		}
		return tags;
	}

	@Override
	public boolean hasTag() {
		return !this.tags.isEmpty();
	}

	@Override
	public String toString() {
		return this.getValue();
	}

	@Override
	public void addTag(StringConstantValueTag tag) {
		this.tags.add(tag);
	}
}
