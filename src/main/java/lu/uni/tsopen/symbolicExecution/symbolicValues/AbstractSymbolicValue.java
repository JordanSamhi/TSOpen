package lu.uni.tsopen.symbolicExecution.symbolicValues;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lu.uni.tsopen.symbolicExecution.ContextualValues;
import lu.uni.tsopen.symbolicExecution.SymbolicExecution;
import soot.Value;
import soot.jimple.Constant;
import soot.tagkit.StringConstantValueTag;

public abstract class AbstractSymbolicValue implements SymbolicValue {

	protected List<StringConstantValueTag> tags;
	protected SymbolicExecution se;
	protected Map<Value, List<SymbolicValue>> values;

	public AbstractSymbolicValue(SymbolicExecution se) {
		this.tags = new ArrayList<StringConstantValueTag>();
		this.se = se;
		this.values = new HashMap<Value, List<SymbolicValue>>();
	}

	protected String computeValue(Value v) {
		List<SymbolicValue> values = null;
		String s = "";
		values = this.values.get(v);
		if(values != null) {
			s += values.get(0);
			return s;
		}else if(v instanceof Constant){
			return ((Constant)v).toString();
		}
		return v.getType().toString();
	}

	protected List<SymbolicValue> getSymbolicValues(Value v) {
		Map<Value, ContextualValues> context = this.se.getContext();
		if(context.containsKey(v)) {
			return context.get(v).getLastCoherentValues(null);
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
		String value = "";
		List<String> usedStrings = new ArrayList<String>();
		String tagValue = null;
		if(!this.tags.isEmpty()) {
			for(StringConstantValueTag tag : this.tags) {
				tagValue = tag.getStringValue();
				if(!usedStrings.contains(tagValue)) {
					usedStrings.add(tagValue);
					value += tag.getStringValue();
					if(tag != this.tags.get(this.tags.size() - 1)) {
						value += " | ";
					}
				}
			}
			return value;
		}
		return this.getValue();
	}

	@Override
	public void addTag(StringConstantValueTag tag) {
		this.tags.add(tag);
	}

	@Override
	public boolean containsTag(String t) {
		for(StringConstantValueTag tag : this.tags) {
			if(tag.getStringValue().equals(t)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Value getBase() {
		return null;
	}
}
