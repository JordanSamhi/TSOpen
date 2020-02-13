package lu.uni.tsopen.utils;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import lu.uni.tsopen.logicBombs.PotentialLogicBombsRecovery;
import lu.uni.tsopen.pathPredicateRecovery.PathPredicateRecovery;
import lu.uni.tsopen.symbolicExecution.ContextualValues;
import lu.uni.tsopen.symbolicExecution.SymbolicExecution;
import lu.uni.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import soot.FastHierarchy;
import soot.MethodOrMethodContext;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.DefinitionStmt;
import soot.jimple.IfStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;
import soot.jimple.internal.IdentityRefBox;
import soot.jimple.toolkits.callgraph.Edge;
import soot.tagkit.StringConstantValueTag;
import soot.toolkits.graph.DominatorTree;
import soot.toolkits.graph.MHGDominatorsFinder;

public class Utils {

	protected static Logger logger = LoggerFactory.getLogger(Utils.class);

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

	public static boolean containsTags(Value v, SymbolicExecution se) {
		List<SymbolicValue> values = getSymbolicValues(v, se);
		if(values != null) {
			for(SymbolicValue sv : values) {
				if(sv.hasTag()) {
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
				values = contextualValues.getAllValues();
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

	private static List<SootClass> getAllSuperClasses(SootClass sootClass) {
		List<SootClass> classes = new ArrayList<SootClass>();
		if (sootClass.hasSuperclass()) {
			classes.add(sootClass.getSuperclass());
			classes.addAll(getAllSuperClasses(sootClass.getSuperclass()));
		}
		return classes;
	}

	public static String getComponentType(SootClass sc) {
		List<SootClass> classes = getAllSuperClasses(sc);
		for(SootClass c : classes) {
			switch (c.getName()) {
			case Constants.ANDROID_APP_ACTIVITY : return Constants.ACTIVITY;
			case Constants.ANDROID_CONTENT_BROADCASTRECEIVER : return Constants.BROADCAST_RECEIVER;
			case Constants.ANDROID_CONTENT_CONTENTPROVIDER : return Constants.CONTENT_PROVIDER;
			case Constants.ANDROID_APP_SERVICE : return Constants.SERVICE;
			}
		}
		return Constants.BASIC_CLASS;
	}

	public static boolean isInCallGraph(SootMethod m) {
		MethodOrMethodContext next = null;
		Iterator<MethodOrMethodContext> itMethod = Scene.v().getCallGraph().sourceMethods();
		Iterator<soot.jimple.toolkits.callgraph.Edge> itEdge = null;
		soot.jimple.toolkits.callgraph.Edge e = null;
		while(itMethod.hasNext()){
			next = itMethod.next();
			itEdge = Scene.v().getCallGraph().edgesOutOf(next);
			while(itEdge.hasNext()) {
				e = itEdge.next();
				if(e.tgt().equals(m)) {
					return true;
				}
			}
		}
		return false;
	}

	public static int getGuardedBlocksDensity(PathPredicateRecovery ppr, IfStmt ifStmt) {
		return ppr.getGuardedBlocks(ifStmt).size();
	}

	public static boolean guardedBlocksContainApplicationInvoke(PathPredicateRecovery ppr, IfStmt ifStmt) {
		SootMethod m = null;
		for(Unit u : ppr.getGuardedBlocks(ifStmt)) {
			if(u instanceof InvokeStmt) {
				m = ((InvokeStmt) u).getInvokeExpr().getMethod();
				if(m.getDeclaringClass().isApplicationClass()) {
					return true;
				}
			}
		}
		return false;
	}

	public static <T> String join(String sep, List<T> list) {
		String s = "(";
		for(int i = 0 ; i < list.size() ; i++) {
			s += list.get(i).toString();
			if(i != list.size()-1) {
				s += sep;
			}
		}
		s += ")";
		return s;
	}

	public static String getStartingComponent(SootMethod method) {
		return Utils.getComponentType(Scene.v().getSootClass(Lists.reverse(getLogicBombCallStack(method)).get(1).getReturnType().toString()));
	}

	public static List<SootMethod> getLogicBombCallStack(SootMethod m){
		Iterator<Edge> it = Scene.v().getCallGraph().edgesInto(m);
		Edge next = null;
		List<SootMethod> methods = new LinkedList<SootMethod>();
		methods.add(m);

		while(it.hasNext()) {
			next = it.next();
			methods.addAll(getLogicBombCallStack(next.src()));
			return methods;
		}
		return methods;
	}

	public static List<Integer> getLengthLogicBombCallStack(SootMethod m, Integer c, List<Integer> l, List<SootMethod> visitedMethods) {
		Iterator<Edge> it = Scene.v().getCallGraph().edgesInto(m);
		Edge next = null;

		visitedMethods.add(m);
		if(!it.hasNext()) {
			l.add(c.intValue());
		}

		while(it.hasNext()) {
			next = it.next();
			if(visitedMethods.contains(next.src())){
				continue;
			}
			c += 1;
			getLengthLogicBombCallStack(next.src(), c, l, visitedMethods);
			visitedMethods.remove(m);
			c -= 1;
		}
		return l;
	}

	public static List<Integer> getLengthLogicBombCallStack(SootMethod m) {
		return getLengthLogicBombCallStack(m, 0, new ArrayList<Integer>(), new ArrayList<SootMethod>());
	}

	public static boolean isSensitiveMethod(SootMethod m) {
		InputStream fis = null;
		BufferedReader br = null;
		String line = null;
		try {
			fis = Utils.class.getResourceAsStream(Constants.SENSITIVE_METHODS_FILE);
			br = new BufferedReader(new InputStreamReader(fis));
			while ((line = br.readLine()) != null)   {
				if(m.getSignature().equals(line)) {
					br.close();
					fis.close();
					return true;
				}
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		try {
			br.close();
			fis.close();
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return false;
	}

	public static boolean isNested(IfStmt ifStmt, InfoflowCFG icfg, PotentialLogicBombsRecovery plbr, PathPredicateRecovery ppr) {
		Map<IfStmt, Pair<List<SymbolicValue>, SootMethod>> plbs = plbr.getPotentialLogicBombs();
		IfStmt currentIf = null;
		SootMethod method1 = icfg.getMethodOf(ifStmt),
				method2 = null;
		MHGDominatorsFinder<Unit> df = new  MHGDominatorsFinder<Unit>(icfg.getOrCreateUnitGraph(method1));
		DominatorTree<Unit> dt = new DominatorTree<Unit>(df);
		for(Entry<IfStmt, Pair<List<SymbolicValue>, SootMethod>> e : plbs.entrySet()) {
			currentIf = e.getKey();
			method2 = icfg.getMethodOf(currentIf);
			if(ifStmt != currentIf) {
				if(method1 == method2) {
					if(dt.isDominatorOf(dt.getDode(currentIf), dt.getDode(ifStmt))) {
						if(ppr.getGuardedBlocks(currentIf).contains(ifStmt)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	public static boolean isFilteredLib(SootClass sc) {
		InputStream fis = null;
		BufferedReader br = null;
		String line = null;
		try {
			fis = Utils.class.getResourceAsStream(Constants.FILTERED_LIBS);
			br = new BufferedReader(new InputStreamReader(fis));
			while ((line = br.readLine()) != null)   {
				if(sc.getName().startsWith(line)) {
					br.close();
					fis.close();
					return true;
				}
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		try {
			br.close();
			fis.close();
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return false;
	}
}
