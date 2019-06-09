package com.github.dusby.tsopen.utils;

import soot.Unit;
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
}
