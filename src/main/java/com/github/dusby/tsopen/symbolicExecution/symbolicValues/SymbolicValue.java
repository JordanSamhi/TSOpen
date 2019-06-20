package com.github.dusby.tsopen.symbolicExecution.symbolicValues;

import java.util.List;

import soot.tagkit.StringConstantValueTag;

public interface SymbolicValue {
	public String getValue();
	public boolean isSymbolic();
	public boolean isConstant();
	public boolean isMethodRepresentation();
	public boolean isObject();
	public boolean hasTag();
	public String getStringTags();
	public void addTag(StringConstantValueTag scvt);
	public List<StringConstantValueTag> getTags();
}