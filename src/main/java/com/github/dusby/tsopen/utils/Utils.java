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

	public static boolean containsTag(Value base, String nowTag, SymbolicExecution se) {
		List<SymbolicValue> values = null;
		ContextualValues contextualValues = null;
		if(base != null) {
			contextualValues = se.getContext().get(base);
			if(contextualValues != null) {
				values = contextualValues.getLastCoherentValues();
				for(SymbolicValue sv : values) {
					if(sv.containsTag(nowTag)) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
