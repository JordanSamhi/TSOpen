package com.github.dusby.tsopen.logicBombs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javatuples.Sextet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dusby.tsopen.pathPredicateRecovery.PathPredicateRecovery;
import com.github.dusby.tsopen.pathPredicateRecovery.SimpleBlockPredicateExtraction;
import com.github.dusby.tsopen.symbolicExecution.ContextualValues;
import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import com.github.dusby.tsopen.utils.Constants;

import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.ConditionExpr;
import soot.jimple.Constant;
import soot.jimple.DefinitionStmt;
import soot.jimple.IfStmt;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.NullConstant;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;

public class PotentialLogicBombsRecovery implements Runnable {

	private final SimpleBlockPredicateExtraction sbpe;
	private final SymbolicExecution se;
	private final PathPredicateRecovery ppr;
	private Map<IfStmt, List<SymbolicValue>> potentialLogicBombs;
	private List<SootMethod> visitedMethods;
	private List<IfStmt> visitedIfs;
	private InfoflowCFG icfg;

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	public PotentialLogicBombsRecovery(SimpleBlockPredicateExtraction sbpe, SymbolicExecution se, PathPredicateRecovery ppr, InfoflowCFG icfg) {
		this.sbpe = sbpe;
		this.se = se;
		this.ppr = ppr;
		this.visitedMethods = new ArrayList<SootMethod>();
		this.visitedIfs = new ArrayList<IfStmt>();
		this.potentialLogicBombs = new HashMap<IfStmt, List<SymbolicValue>>();
		this.icfg = icfg;
	}

	@Override
	public void run() {
		this.retrievePotentialLogicBombs();
	}

	private void retrievePotentialLogicBombs() {
		for(IfStmt ifStmt : this.sbpe.getConditions()) {
			if(!this.isTrigger(ifStmt)) {
				this.potentialLogicBombs.remove(ifStmt);
			}
		}
	}

	private boolean isTrigger(IfStmt ifStmt) {
		this.visitedMethods.clear();
		if(!this.isSuspicious(ifStmt)) {
			return false;
		}
		if(this.logger.isDebugEnabled()) {
			this.logger.debug("Predicate is suspicious : {}", ifStmt);
		}
		if(!this.isSuspiciousAfterPostFilters(ifStmt)) {
			return false;
		}
		if(this.logger.isDebugEnabled()) {
			this.logger.debug("Predicate is suspicious after post filters : {}", ifStmt);
		}
		if(this.controlSensitiveAction(ifStmt)) {
			return true;
		}
		if(this.logger.isDebugEnabled()) {
			this.logger.debug("Predicate does not control sensitive action : {}", ifStmt);
		}
		return false;
	}

	private boolean isSuspiciousAfterPostFilters(IfStmt ifStmt) {
		Sextet<List<SymbolicValue>, List<SymbolicValue>, List<SymbolicValue>, Value, Value, Constant> contextualValues = this.getContextualValues(ifStmt);
		List<SymbolicValue> values = contextualValues.getValue0();
		Constant constant = contextualValues.getValue5();

		if(values != null) {
			for(SymbolicValue sv : values) {
				if((sv.containsTag(Constants.SECONDS_TAG)
						|| sv.containsTag(Constants.MINUTES_TAG)
						|| sv.containsTag(Constants.MONTH_TAG)
						|| sv.containsTag(Constants.HOUR_TAG)
						|| sv.containsTag(Constants.YEAR_TAG)
						|| sv.containsTag(Constants.LONGITUDE_TAG)
						|| sv.containsTag(Constants.LATITUDE_TAG)
						|| sv.containsTag(Constants.CURRENT_TIME_MILLIS)
						|| sv.containsTag(Constants.NOW_TAG)
						|| sv.containsTag(Constants.HERE_TAG)
						|| sv.containsTag(Constants.SMS_TAG)
						|| sv.containsTag(Constants.SMS_SENDER_TAG)
						|| sv.containsTag(Constants.SMS_BODY_TAG)
						|| sv.containsTag(Constants.SUSPICIOUS))
						&& (constant != null)
						&& (constant instanceof IntConstant && ((IntConstant)constant).value == -1
						|| (constant instanceof NullConstant))) {
					continue;
				}else if(!sv.hasTag()) {
					continue;
				}
				this.addPotentialLogicBomb(ifStmt, sv);
			}
		}
		return true;
	}

	private void addPotentialLogicBomb(IfStmt ifStmt, SymbolicValue sv) {
		List<SymbolicValue> lbs = this.potentialLogicBombs.get(ifStmt);
		if(lbs == null) {
			lbs = new ArrayList<SymbolicValue>();
			this.potentialLogicBombs.put(ifStmt, lbs);
		}
		lbs.add(sv);
	}

	private boolean controlSensitiveAction(IfStmt ifStmt) {
		List<Unit> guardedBlocks = this.ppr.getGuardedBlocks(ifStmt);
		if(this.isSensitive(guardedBlocks)) {
			return true;
		}
		for(Unit block : guardedBlocks) {
			for(ValueBox vb : block.getDefBoxes()) {
				for(IfStmt i : this.getRelatedPredicates(vb.getValue())) {
					if(ifStmt != i && !this.visitedIfs.contains(i)) {
						this.visitedIfs.add(i);
						if(this.controlSensitiveAction(i)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	private List<IfStmt> getRelatedPredicates(Value value) {
		List<IfStmt> ifs = new ArrayList<IfStmt>();
		for(IfStmt ifStmt : this.sbpe.getConditions()) {
			for(ValueBox vb : ifStmt.getUseBoxes()) {
				if(vb.getValue() == value) {
					ifs.add(ifStmt);
				}
			}
		}
		return ifs;
	}

	private boolean isSensitive(Collection<Unit> guardedBlocks) {
		for(Unit block : guardedBlocks) {
			for(SootMethod m : this.getInvokedMethods(block)) {
				if(!this.visitedMethods.contains(m)) {
					this.visitedMethods.add(m);
					if(this.isSensitiveMethod(m)) {
						return true;
					}
					if(m.getDeclaringClass().isApplicationClass() && this.isSensitive(m.retrieveActiveBody().getUnits())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private Collection<SootMethod> getInvokedMethods(Unit block) {
		Collection<SootMethod> methods = new ArrayList<SootMethod>();
		DefinitionStmt defUnit = null;
		Value value = null;
		if(block instanceof InvokeStmt) {
			methods.addAll(this.icfg.getCalleesOfCallAt(block));
		}else if(block instanceof DefinitionStmt) {
			defUnit = (DefinitionStmt) block;
			if(defUnit.getRightOp() instanceof InvokeExpr) {
				methods.addAll(this.icfg.getCalleesOfCallAt(defUnit));
			}
		}
		if(methods.isEmpty()) {
			for(ValueBox v : block.getUseAndDefBoxes()) {
				value = v.getValue();
				if(value instanceof InvokeExpr) {
					methods.add(((InvokeExpr)value).getMethod());
				}
			}
		}
		return methods;
	}

	private boolean isSensitiveMethod(SootMethod m) {
		InputStream fis = null;
		BufferedReader br = null;
		String line = null;
		try {
			fis = this.getClass().getResourceAsStream(Constants.SENSITIVE_METHODS_FILE);
			br = new BufferedReader(new InputStreamReader(fis));
			while ((line = br.readLine()) != null)   {
				if(m.getSignature().equals(line)) {
					br.close();
					fis.close();
					return true;
				}
			}
		} catch (IOException e) {
			this.logger.error(e.getMessage());
		}
		try {
			br.close();
			fis.close();
		} catch (IOException e) {
			this.logger.error(e.getMessage());
		}
		return false;
	}

	private boolean isSuspicious(IfStmt ifStmt) {
		Sextet<List<SymbolicValue>, List<SymbolicValue>, List<SymbolicValue>, Value, Value, Constant> contextualValues = this.getContextualValues(ifStmt);
		List<SymbolicValue> values = contextualValues.getValue0(),
				valuesOp1 = contextualValues.getValue1(),
				valuesOp2 = contextualValues.getValue2();
		Value op1 = contextualValues.getValue3(),
				op2 = contextualValues.getValue4();

		if(values != null) {
			for(SymbolicValue sv : values) {
				if(sv.containsTag(Constants.SECONDS_TAG)
						|| sv.containsTag(Constants.MINUTES_TAG)
						|| sv.containsTag(Constants.HOUR_TAG)
						|| sv.containsTag(Constants.YEAR_TAG)
						|| sv.containsTag(Constants.MONTH_TAG)
						|| sv.containsTag(Constants.LONGITUDE_TAG)
						|| sv.containsTag(Constants.LATITUDE_TAG)
						|| sv.containsTag(Constants.CURRENT_TIME_MILLIS)
						|| sv.containsTag(Constants.NOW_TAG)
						|| sv.containsTag(Constants.SUSPICIOUS)) {
					return true;
				}
			}
		}
		if(!(op1 instanceof Constant) && !(op2 instanceof Constant)) {
			if(valuesOp1 != null) {
				for(SymbolicValue sv1 : valuesOp1) {
					if(sv1.containsTag(Constants.HERE_TAG)
							|| sv1.containsTag(Constants.NOW_TAG)
							|| sv1.containsTag(Constants.HOUR_TAG)
							|| sv1.containsTag(Constants.YEAR_TAG)
							|| sv1.containsTag(Constants.LATITUDE_TAG)
							|| sv1.containsTag(Constants.LONGITUDE_TAG)
							|| sv1.containsTag(Constants.MINUTES_TAG)
							|| sv1.containsTag(Constants.SECONDS_TAG)
							|| sv1.containsTag(Constants.MONTH_TAG)) {
						return true;
					}
				}
			}
			if(valuesOp2 != null) {
				for(SymbolicValue sv2 : valuesOp2) {
					if(sv2.containsTag(Constants.HERE_TAG)
							|| sv2.containsTag(Constants.HOUR_TAG)
							|| sv2.containsTag(Constants.YEAR_TAG)
							|| sv2.containsTag(Constants.NOW_TAG)
							|| sv2.containsTag(Constants.LATITUDE_TAG)
							|| sv2.containsTag(Constants.LONGITUDE_TAG)
							|| sv2.containsTag(Constants.MINUTES_TAG)
							|| sv2.containsTag(Constants.SECONDS_TAG)
							|| sv2.containsTag(Constants.MONTH_TAG)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private Sextet<List<SymbolicValue>, List<SymbolicValue>, List<SymbolicValue>, Value, Value, Constant> getContextualValues(IfStmt ifStmt) {
		ConditionExpr conditionExpr = (ConditionExpr) ifStmt.getCondition();
		Value op1 = conditionExpr.getOp1(),
				op2 = conditionExpr.getOp2();
		ContextualValues contextualValuesOp1 = null,
				contextualValuesOp2 = null;
		List<SymbolicValue> valuesOp1 = null,
				valuesOp2 = null,
				values = null;
		Constant constant = null;
		if(!(op1 instanceof Constant)) {
			contextualValuesOp1 = this.se.getContextualValues(op1);
		}
		if(!(op2 instanceof Constant)) {
			contextualValuesOp2 = this.se.getContextualValues(op2);
		}
		if(contextualValuesOp1 != null) {
			valuesOp1 = contextualValuesOp1.getLastCoherentValues(ifStmt);
		}
		if(contextualValuesOp2 != null) {
			valuesOp2 = contextualValuesOp2.getLastCoherentValues(ifStmt);
		}
		if(valuesOp1 != null && (op2 instanceof Constant)) {
			values = valuesOp1;
			constant = (Constant) op2;
		}else if (valuesOp2 != null && (op1 instanceof Constant)) {
			values = valuesOp2;
			constant = (Constant) op1;
		}
		return new Sextet<List<SymbolicValue>, List<SymbolicValue>, List<SymbolicValue>, Value, Value, Constant>(values, valuesOp1, valuesOp2, op1, op2, constant);
	}

	public Map<IfStmt, List<SymbolicValue>> getPotentialLogicBombs(){
		return this.potentialLogicBombs;
	}

	public boolean hasPotentialLogicBombs() {
		return !this.potentialLogicBombs.isEmpty();
	}
}
