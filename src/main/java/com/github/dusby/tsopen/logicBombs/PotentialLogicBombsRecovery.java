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
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.ConstantValue;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import com.github.dusby.tsopen.utils.Constants;
import com.github.dusby.tsopen.utils.Utils;

import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AssignStmt;
import soot.jimple.ConditionExpr;
import soot.jimple.Constant;
import soot.jimple.IfStmt;
import soot.jimple.IntConstant;
import soot.jimple.NullConstant;
import soot.jimple.ReturnStmt;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;

public class PotentialLogicBombsRecovery implements Runnable {

	private final SimpleBlockPredicateExtraction sbpe;
	private final SymbolicExecution se;
	private final PathPredicateRecovery ppr;
	private Map<IfStmt, List<SymbolicValue>> potentialLogicBombs;
	private List<SootMethod> visitedMethods;
	private List<IfStmt> visitedIfs;
	private InfoflowCFG icfg;
	private boolean containsSuspiciousCheck;
	private boolean containsSuspiciousCheckAfterControlDependency;
	private boolean containsSuspiciousCheckAfterPostFilterStep;

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	public PotentialLogicBombsRecovery(SimpleBlockPredicateExtraction sbpe, SymbolicExecution se, PathPredicateRecovery ppr, InfoflowCFG icfg) {
		this.sbpe = sbpe;
		this.se = se;
		this.ppr = ppr;
		this.visitedMethods = new ArrayList<SootMethod>();
		this.visitedIfs = new ArrayList<IfStmt>();
		this.potentialLogicBombs = new HashMap<IfStmt, List<SymbolicValue>>();
		this.icfg = icfg;
		this.containsSuspiciousCheck = false;
		this.containsSuspiciousCheckAfterControlDependency = false;
		this.containsSuspiciousCheckAfterPostFilterStep = false;
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
		this.containsSuspiciousCheck = true;
		if(this.logger.isDebugEnabled()) {
			this.logger.debug("Predicate is suspicious : {}", ifStmt);
		}
		if(!this.controlSensitiveAction(ifStmt)) {
			return false;
		}
		this.containsSuspiciousCheckAfterControlDependency = true;
		if(this.logger.isDebugEnabled()) {
			this.logger.debug("Predicate is suspicious after post filters : {}", ifStmt);
		}
		if(this.isSuspiciousAfterPostFilters(ifStmt)) {
			this.containsSuspiciousCheckAfterPostFilterStep = true;
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
		if(this.ifControlBoolReturn(ifStmt)) {
			SootMethod methodOfIf = this.icfg.getMethodOf(ifStmt);
			AssignStmt callerAssign = null;
			Value leftOp = null;
			for(Unit caller : this.icfg.getCallersOf(methodOfIf)) {
				if(caller instanceof AssignStmt) {
					callerAssign = (AssignStmt) caller;
					leftOp = callerAssign.getLeftOp();
					for(Unit u : this.icfg.getSuccsOf(callerAssign)) {
						if(u instanceof IfStmt) {
							for(ValueBox vb : u.getUseBoxes()) {
								if(vb.getValue() == leftOp) {
									guardedBlocks = this.ppr.getGuardedBlocks((IfStmt)u);
									if(this.isSensitive(guardedBlocks)) {
										return true;
									}
								}
							}
						}
					}
				}
			}
		}
		return false;
	}

	private boolean ifControlBoolReturn(IfStmt ifStmt) {
		ReturnStmt ret = null;
		Value retOp = null;
		IntConstant retCons = null;
		boolean controlBoolReturn = false;
		for(Unit u : this.icfg.getSuccsOf(ifStmt)) {
			if(u instanceof ReturnStmt) {
				ret = (ReturnStmt)u;
				retOp = ret.getOp();
				if(retOp instanceof IntConstant) {
					retCons = (IntConstant) retOp;
					if(retCons.value == 1 || retCons.value == 0) {
						controlBoolReturn = true;
					}else {
						controlBoolReturn = false;
					}
				}
			}
		}
		return controlBoolReturn;
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
		Collection<Unit> units = null;
		for(Unit block : guardedBlocks) {
			for(SootMethod m : Utils.getInvokedMethods(block, this.icfg)) {
				if(!this.visitedMethods.contains(m)) {
					this.visitedMethods.add(m);
					if(this.isSensitiveMethod(m)) {
						return true;
					}
					if(m.getDeclaringClass().isApplicationClass() && m.isConcrete()) {
						units = m.retrieveActiveBody().getUnits();
						if(units != null) {
							if(this.isSensitive(units)) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
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
		if(valuesOp1 != null) {
			values = valuesOp1;
			if(op2 instanceof Constant) {
				constant = (Constant) op2;
			}else if(this.containConstantSymbolicValue(op2)) {
				constant = this.getConstantValue(op2);
			}
		}else if (valuesOp2 != null) {
			values = valuesOp2;
			if(op1 instanceof Constant) {
				constant = (Constant) op1;
			}else if(this.containConstantSymbolicValue(op1)) {
				constant = this.getConstantValue(op1);
			}
		}
		return new Sextet<List<SymbolicValue>, List<SymbolicValue>, List<SymbolicValue>, Value, Value, Constant>(values, valuesOp1, valuesOp2, op1, op2, constant);
	}

	private boolean containConstantSymbolicValue(Value v) {
		List<SymbolicValue> values = null;
		ContextualValues contextualValues = null;
		if(v != null) {
			contextualValues = this.se.getContext().get(v);
			if(contextualValues != null) {
				values = contextualValues.getAllValues();
				if(values != null) {
					for(SymbolicValue sv: values) {
						if(sv instanceof ConstantValue) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	private Constant getConstantValue(Value v) {
		List<SymbolicValue> values = null;
		ContextualValues contextualValues = null;
		ConstantValue cv = null;
		Constant c = null;
		if(v != null) {
			contextualValues = this.se.getContext().get(v);
			if(contextualValues != null) {
				values = contextualValues.getAllValues();
				if(values != null) {
					for(SymbolicValue sv: values) {
						if(sv instanceof ConstantValue) {
							cv = (ConstantValue)sv;
							c = cv.getConstant();
							if(c instanceof IntConstant) {
								return IntConstant.v(((IntConstant) c).value);
							}
						}
					}
				}
			}
		}
		return null;
	}

	public Map<IfStmt, List<SymbolicValue>> getPotentialLogicBombs(){
		return this.potentialLogicBombs;
	}

	public boolean hasPotentialLogicBombs() {
		return !this.potentialLogicBombs.isEmpty();
	}

	public boolean ContainsSuspiciousCheck() {
		return this.containsSuspiciousCheck;
	}

	public boolean ContainsSuspiciousCheckAfterControlDependency() {
		return this.containsSuspiciousCheckAfterControlDependency;
	}

	public boolean ContainsSuspiciousCheckAfterPostFilterStep() {
		return this.containsSuspiciousCheckAfterPostFilterStep;
	}
}
