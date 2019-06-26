package com.github.dusby.tsopen.utils;

import java.util.List;

import com.github.dusby.tsopen.symbolicExecution.ContextualValues;
import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;

import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.internal.IdentityRefBox;
import soot.tagkit.StringConstantValueTag;

public class Utils {

	/**
	 * Check whether the unit is catching
	 * an exception, useful for predicate recovery.
	 * @param u the unit to check
	 * @return true if u catches an exception, false otherwise
	 */
	public static boolean isCaughtException(Unit u) {
		for(ValueBox useBox : u.getUseBoxes()) {
			if(useBox instanceof IdentityRefBox) {
				if(((IdentityRefBox) useBox).getValue() instanceof CaughtExceptionRef) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean containsTag(Value v, String tag, SymbolicExecution se) {
		List<SymbolicValue> values = getSymbolicValues(v, se);
		if(values != null) {
			for(SymbolicValue sv : values) {
				if(sv.containsTag(tag)) {
					return true;
				}
			}
		}
		return false;
	}

	public static void propagateTags(Value src, SymbolicValue dst, SymbolicExecution se) {
		List<SymbolicValue> values = getSymbolicValues(src, se);
		for(SymbolicValue sv : values) {
			if(sv.hasTag()) {
				for(StringConstantValueTag t : sv.getTags()) {
					if(!dst.containsTag(t.getStringValue())) {
						dst.addTag(new StringConstantValueTag(t.getStringValue()));
					}
				}
			}
		}
	}

	private static List<SymbolicValue> getSymbolicValues(Value v, SymbolicExecution se) {
		List<SymbolicValue> values = null;
		ContextualValues contextualValues = null;
		if(v != null) {
			contextualValues = se.getContext().get(v);
			if(contextualValues != null) {
				values = contextualValues.getLastCoherentValues();
			}
		}
		return values;
	}
}
