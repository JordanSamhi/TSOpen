package predicates;

import java.util.HashMap;
import java.util.Map;

import soot.Value;
import soot.jimple.ConditionExpr;

public class Predicate implements PredicateProvider{

	private Value leftOp, rightOp;
	private String symbol;
	private ConditionExpr conditionExpression;
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

	public Predicate(ConditionExpr conditionExpression, boolean branch) {
		this.setConditionExpression(conditionExpression);
		this.setLeftOp(conditionExpression.getOp1());
		this.setRightOp(conditionExpression.getOp2());
		this.setBranch(branch);
		String condExprSymbol = conditionExpression.getSymbol().trim();
		if(branch) {
			this.setSymbol(condExprSymbol);
		}else {
			this.setSymbol(this.notOperators.get(condExprSymbol));
		}
	}

	@Override
	public String toString() {
		return this.leftOp+" "+this.symbol+" "+this.rightOp;
	}

	@Override
	public void printPredicate() {
		System.out.println(this);
	}

	public Value getLeftOp() {
		return leftOp;
	}

	public void setLeftOp(Value leftOp) {
		this.leftOp = leftOp;
	}

	public Value getRightOp() {
		return rightOp;
	}

	public void setRightOp(Value rightOp) {
		this.rightOp = rightOp;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public boolean isBranch() {
		return branch;
	}

	public void setBranch(boolean branch) {
		this.branch = branch;
	}

	public ConditionExpr getConditionExpression() {
		return conditionExpression;
	}

	public void setConditionExpression(ConditionExpr conditionExpression) {
		this.conditionExpression = conditionExpression;
	}
}
