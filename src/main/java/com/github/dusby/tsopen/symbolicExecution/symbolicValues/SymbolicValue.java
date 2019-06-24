package com.github.dusby.tsopen.symbolicExecution.symbolicValues;

import java.util.List;

import soot.Value;
import soot.tagkit.StringConstantValueTag;

public interface SymbolicValue {
	public String getValue();
	public boolean isSymbolic();
	public boolean isConstant();
	public boolean isMethodRepresentation();
	public boolean isObject();
	public boolean hasTag();
	public void addTag(StringConstantValueTag scvt);
	public List<StringConstantValueTag> getTags();
	public String getStringTags();
	public boolean containsTag(String t);
	public Value getBase();
}