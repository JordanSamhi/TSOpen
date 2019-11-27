package lu.uni.tsopen.symbolicExecution.symbolicValues;

/*-
 * #%L
 * TSOpen - Open-source implementation of TriggerScope
 * 
 * Paper describing the approach : https://seclab.ccs.neu.edu/static/publications/sp2016triggerscope.pdf
 * 
 * %%
 * Copyright (C) 2019 Jordan Samhi
 * University of Luxembourg - Interdisciplinary Centre for
 * Security Reliability and Trust (SnT) - All rights reserved
 *
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

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
