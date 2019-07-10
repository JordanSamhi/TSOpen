package com.github.dusby.tsopen.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import com.github.dusby.tsopen.symbolicExecution.ContextualValues;
import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;

import soot.FastHierarchy;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.DefinitionStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;
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
		if(values != null) {
			for(SymbolicValue sv : values) {
				if(sv != null) {
					if(sv.hasTag()) {
						for(StringConstantValueTag t : sv.getTags()) {
							if(!dst.containsTag(t.getStringValue())) {
								dst.addTag(new StringConstantValueTag(t.getStringValue()));
							}
						}
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
			if(contextualValues == null && v instanceof InstanceFieldRef) {
				for(Entry<Value, ContextualValues> e : se.getContext().entrySet()) {
					if(e.getKey().toString().contains(v.toString())) {
						contextualValues = se.getContext().get(e.getKey());
					}
				}
			}
			if(contextualValues != null) {
				values = contextualValues.getLastCoherentValues(null);
				if(values == null) {
					values = contextualValues.getAllValues();
				}
			}
		}
		return values;
	}

	public static String getFormattedTime(long time) {
		long millis = TimeUnit.MILLISECONDS.convert(time, TimeUnit.NANOSECONDS),
				seconds = 0,
				minutes = 0,
				hours = 0;
		String strTime = "";
		if(millis >= 1000) {
			seconds = millis / 1000;
			if(seconds >= 60) {
				minutes = seconds / 60;
				if(minutes >=60) {
					hours = minutes / 60;
					strTime += String.format("%3s %s", hours, hours > 1 ? "hours" : "hour");
				}else {
					strTime += String.format("%3s %s", minutes, minutes > 1 ? "mins" : "min");
				}
			}else {
				strTime += String.format("%3s %s", seconds, "s");
			}
		}else {
			strTime += String.format("%3s %s", millis, "ms");
		}
		return strTime;
	}

	public static Collection<SootMethod> getInvokedMethods(Unit block, InfoflowCFG icfg) {
		FastHierarchy fh = Scene.v().getOrMakeFastHierarchy();
		Collection<SootClass> classes = null;
		Collection<SootMethod> methods = new ArrayList<SootMethod>();
		SootMethod method = null;
		DefinitionStmt defUnit = null;
		Value value = null;
		if(block instanceof InvokeStmt) {
			methods.addAll(icfg.getCalleesOfCallAt(block));
		}else if(block instanceof DefinitionStmt) {
			defUnit = (DefinitionStmt) block;
			if(defUnit.getRightOp() instanceof InvokeExpr) {
				methods.addAll(icfg.getCalleesOfCallAt(defUnit));
			}
		}
		if(methods.isEmpty()) {
			for(ValueBox v : block.getUseAndDefBoxes()) {
				value = v.getValue();
				if(value instanceof InvokeExpr) {
					method = ((InvokeExpr)value).getMethod();
					if(method.isAbstract()) {
						classes = fh.getSubclassesOf(method.getDeclaringClass());
						for(SootClass c : classes) {
							for(SootMethod m : c.getMethods()) {
								if(m.getSubSignature().equals(method.getSubSignature())) {
									if(!methods.contains(m)) {
										methods.add(m);
									}
								}
							}
						}
					}else {
						methods.add(method);
					}
				}
			}
		}
		return methods;
	}

	public static boolean isDummy(SootMethod m) {
		return m.getName().startsWith("dummyMainMethod");
	}
}
