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

import java.util.List;

import lu.uni.tsopen.symbolicExecution.SymbolicExecution;
import soot.SootMethod;
import soot.Value;
import soot.jimple.Constant;

public class MethodRepresentationValue extends AbstractSymbolicValue {

	private Value base;
	private List<Value> args;
	private SootMethod method;

	public MethodRepresentationValue(Value b, List<Value> a, SootMethod m, SymbolicExecution se) {
		super(se);
		this.method = m;
		this.base = b;
		this.args = a;

		if(this.base != null) {
			this.values.put(this.base, this.getSymbolicValues(this.base));
		}
		for(Value arg : this.args) {
			if(!(arg instanceof Constant)) {
				this.values.put(arg, this.getSymbolicValues(arg));
			}
		}
	}

	@Override
	public String getValue() {
		String value = "(";
		if(this.base != null) {
			value += this.computeValue(this.base);
			value += ".";
		}
		value += this.method.getName();
		value += "(";
		for(Value arg : this.args) {
			value += this.computeValue(arg);
			if(arg != this.args.get(this.args.size() - 1)) {
				value += ", ";
			}
		}
		value += "))";
		return value;
	}

	@Override
	public boolean isSymbolic() {
		return false;
	}

	@Override
	public boolean isConstant() {
		return false;
	}

	@Override
	public boolean isMethodRepresentation() {
		return true;
	}

	@Override
	public boolean isObject() {
		return false;
	}

	@Override
	public Value getBase() {
		return this.base;
	}

	public List<Value> getArgs() {
		return this.args;
	}
}
