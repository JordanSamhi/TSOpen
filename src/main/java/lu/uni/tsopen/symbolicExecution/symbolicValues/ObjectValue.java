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

import java.util.ArrayList;
import java.util.List;

import lu.uni.tsopen.symbolicExecution.SymbolicExecution;
import soot.Type;
import soot.Value;

public class ObjectValue extends ConcreteValue {

	private Type type;
	private List<Value> args;

	public ObjectValue(Type t, List<Value> args, SymbolicExecution se) {
		super(se);
		this.type = t;
		this.args = args == null ? new ArrayList<Value>() : args;
	}

	@Override
	public String getValue() {
		return String.format("%s(%s)", this.type, this.computeArgs());
	}

	private String computeArgs() {
		String args = "";
		List<SymbolicValue> values = null;
		for(Value arg : this.args) {
			values = this.getSymbolicValues(arg);
			if(values != null) {
				for(SymbolicValue sv : values) {
					args += sv;
					if(sv != values.get(values.size() - 1)) {
						args += " | ";
					}
				}
			}else {
				args += arg.getType();
			}
			if(arg != this.args.get(this.args.size() - 1)) {
				args += ", ";
			}
		}
		return args;
	}

	@Override
	public boolean isConstant() {
		return false;
	}

	@Override
	public boolean isObject() {
		return true;
	}
}
