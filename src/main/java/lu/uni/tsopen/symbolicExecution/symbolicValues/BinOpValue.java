package lu.uni.tsopen.symbolicExecution.symbolicValues;

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
