
package com.github.dusby.predicates;

import java.util.HashMap;
import java.util.Map;

import soot.Value;
import soot.jimple.ConditionExpr;
import soot.jimple.IfStmt;

public class Predicate implements PredicateProvider{

	private Value leftOp, rightOp;
	private String symbol;
	private ConditionExpr conditionExpression;
	private IfStmt ifStmt;
	boolean branch;

	@SuppressWarnings("serial")
	private Map<String, String> notOperators = new HashMap<String, String>() {{
		put("==", "!=");
		put("!=", "==");
		put("<", ">=");
		put("<=", ">");
		put(">", "<=");
		put(">=", "<");
	}};

	public Predicate(IfStmt ifStmt, boolean branch) {
		ConditionExpr conditionExpression = (ConditionExpr) ifStmt.getCondition();
		this.conditionExpression = conditionExpression;
		this.ifStmt = ifStmt;
		this.leftOp = conditionExpression.getOp1();
		this.rightOp = conditionExpression.getOp2();
		this.branch = branch;
		String condExprSymbol = conditionExpression.getSymbol().trim();
		if(branch) {
			this.symbol = condExprSymbol;
		}else {
			this.symbol = this.notOperators.get(condExprSymbol);
		}
	}
	
	public Predicate(Predicate predicate) {
		this.leftOp = predicate.leftOp;
		this.rightOp = predicate.rightOp;
		this.symbol = predicate.symbol;
		this.conditionExpression = predicate.conditionExpression;
		this.ifStmt = predicate.ifStmt;
		this.branch = predicate.branch;
	}
	
	public boolean isEquivalentTo(PredicateProvider p) {
		if (this == p) {
			return true;
		}
		if(p == null || this.getClass() != p.getClass()) {
			return false;
		}
		Predicate predicate = (Predicate) p;
		return this.ifStmt == predicate.ifStmt && this.branch == predicate.branch;
	}
	
	@Override
	public String toString() {
		return String.format("{} {} {}", this.leftOp, this.symbol, this.rightOp);
	}
	
	public IfStmt getIfStmt() {
		return this.ifStmt;
	}

	@Override
	public String getSymbol() {
		return this.symbol;
	}
}
