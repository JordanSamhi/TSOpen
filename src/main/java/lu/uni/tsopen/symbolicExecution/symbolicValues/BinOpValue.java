package lu.uni.tsopen.symbolicExecution.symbolicValues;

import lu.uni.tsopen.symbolicExecution.SymbolicExecution;
import soot.Value;

public class BinOpValue extends AbstractSymbolicValue {

	private Value op1;
	private Value op2;
	private String symbol;

	public BinOpValue(SymbolicExecution se, Value op1, Value op2, String symbol) {
		super(se);
		this.op1 = op1;
		this.op2 = op2;
		this.symbol = symbol;
		if(this.op1 != null) {
			this.values.put(this.op1, this.getSymbolicValues(this.op1));
		}
		if(this.op2 != null) {
			this.values.put(this.op2, this.getSymbolicValues(this.op2));
		}
	}

	@Override
	public String getValue() {
		return String.format("%s%s%s", this.computeValue(this.op1), this.symbol, this.computeValue(this.op2));
	}

	@Override
	public boolean isSymbolic() {
		return true;
	}

	@Override
	public boolean isConstant() {
		return false;
	}

	@Override
	public boolean isMethodRepresentation() {
		return false;
	}

	@Override
	public boolean isObject() {
		return false;
	}

	public Value getOp1() {
		return this.op1;
	}

	public Value getOp2() {
		return this.op2;
	}
}
