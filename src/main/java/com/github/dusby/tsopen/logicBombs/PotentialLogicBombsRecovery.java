package com.github.dusby.tsopen.logicBombs;

import java.util.ArrayList;
import java.util.List;

import com.github.dusby.tsopen.pathPredicateRecovery.PathPredicateRecovery;
import com.github.dusby.tsopen.pathPredicateRecovery.SimpleBlockPredicateExtraction;
import com.github.dusby.tsopen.symbolicExecution.ContextualValues;
import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import com.github.dusby.tsopen.utils.Constants;

import soot.Unit;
import soot.Value;
import soot.jimple.ConditionExpr;
import soot.jimple.Constant;
import soot.jimple.IfStmt;

public class PotentialLogicBombsRecovery implements Runnable {

	private final SimpleBlockPredicateExtraction sbpe;
	private final SymbolicExecution se;
	private final PathPredicateRecovery ppr;
	private List<IfStmt> potentialLogicBombs;

	public PotentialLogicBombsRecovery(SimpleBlockPredicateExtraction sbpe, SymbolicExecution se, PathPredicateRecovery ppr) {
		this.sbpe = sbpe;
		this.se = se;
		this.ppr = ppr;
		this.potentialLogicBombs = new ArrayList<IfStmt>();
	}

	@Override
	public void run() {
		this.retrievePotentialLogicBombs();
	}

	private void retrievePotentialLogicBombs() {
		for(IfStmt ifStmt : this.sbpe.getConditions()) {
			if(this.isTrigger(ifStmt)) {
				this.potentialLogicBombs.add(ifStmt);
			}
		}
	}

	private boolean isTrigger(IfStmt ifStmt) {
		if(!this.isSuspicious(ifStmt)) {
			return false;
		}
		if(this.controlSensitiveAction(ifStmt)) {
			return true;
		}
		return false;
	}

	private boolean controlSensitiveAction(IfStmt ifStmt) {
		List<Unit> guardedBlocks = this.ppr.getGuardedBlocks(ifStmt);
		if(this.isSensitive(guardedBlocks)) {
			return true;
		}
		for(Unit block : guardedBlocks) {
		}
		return false;
	}

	private boolean isSensitive(List<Unit> guardedBlocks) {
		for(Unit block : guardedBlocks) {
		}
		return false;
	}

	private boolean isSuspicious(IfStmt ifStmt) {
		ConditionExpr conditionExpr = (ConditionExpr) ifStmt.getCondition();
		Value op1 = conditionExpr.getOp1(),
				op2 = conditionExpr.getOp2();
		ContextualValues contextualValuesOp1 = null,
				contextualValuesOp2 = null;
		List<SymbolicValue> valuesOp1 = null,
				valuesOp2 = null,
				values = null;
		if(!(op1 instanceof Constant)) {
			contextualValuesOp1 = this.se.getContextualValues(op1);
		}
		if(!(op2 instanceof Constant)) {
			contextualValuesOp2 = this.se.getContextualValues(op2);
		}
		if(contextualValuesOp1 != null) {
			valuesOp1 = contextualValuesOp1.getAllValues();
		}
		if(contextualValuesOp2 != null) {
			valuesOp2 = contextualValuesOp2.getAllValues();
		}

		if(valuesOp1 != null && (op2 instanceof Constant)) {
			values = valuesOp1;
		}else if (valuesOp2 != null && (op1 instanceof Constant)) {
			values = valuesOp2;
		}

		if(values != null) {
			for(SymbolicValue sv : values) {
				if(sv.containsTag(Constants.SECONDS_TAG)
						|| sv.containsTag(Constants.MINUTES_TAG)
						|| sv.containsTag(Constants.MONTH_TAG)
						|| sv.containsTag(Constants.LONGITUDE_TAG)
						|| sv.containsTag(Constants.LATITUDE_TAG)
						|| sv.containsTag(Constants.CURRENT_TIME_MILLIS)
						|| sv.containsTag(Constants.SUSPICIOUS)) {
					return true;
				}
			}
		}
		return false;
	}

	public List<IfStmt> getPotentialLogicBombs(){
		return this.potentialLogicBombs;
	}
}
