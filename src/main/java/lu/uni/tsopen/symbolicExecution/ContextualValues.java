package lu.uni.tsopen.symbolicExecution;

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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lu.uni.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import soot.Unit;
import soot.Value;

public class ContextualValues {

	private Value receiver;
	private LinkedHashMap<Unit, LinkedList<SymbolicValue>> nodesToSymbolicValues;
	private SymbolicExecution se;

	public ContextualValues(SymbolicExecution se, Value receiver) {
		this.nodesToSymbolicValues = new LinkedHashMap<Unit, LinkedList<SymbolicValue>>();
		this.se = se;
		this.receiver = receiver;
	}

	public void addValue(Unit node, SymbolicValue sv) {
		LinkedList<SymbolicValue> valuesOfNode = this.nodesToSymbolicValues.get(node);
		if(valuesOfNode == null) {
			valuesOfNode = new LinkedList<SymbolicValue>();
			this.nodesToSymbolicValues.put(node, valuesOfNode);
		}
		valuesOfNode.add(sv);
	}

	/**
	 * Return last available values on the current path if possible.
	 * Otherwise the last computed values
	 * @return a list of symbolic values
	 */
	public List<SymbolicValue> getLastCoherentValues(Unit node) {
		Iterator<Unit> it = this.se.getCurrentPath().descendingIterator();
		LinkedList<SymbolicValue> values = null;
		Unit n = null;
		Map<Value, List<SymbolicValue>> valuesAtNode = null;
		if(node == null) {
			while(it.hasNext()) {
				n = it.next();
				if(n != this.se.getCurrentPath().getLast()) {
					values = this.nodesToSymbolicValues.get(n);
					if(values != null) {
						return values;
					}
				}
			}
		}else {
			valuesAtNode = this.se.getValuesAtNode(node);
			if(valuesAtNode != null) {
				return valuesAtNode.get(this.receiver);
			}
		}
		return this.getAllValues();
	}

	public List<SymbolicValue> getAllValues() {
		List<SymbolicValue> values = new ArrayList<SymbolicValue>();
		for(Entry<Unit, LinkedList<SymbolicValue>> e : this.nodesToSymbolicValues.entrySet()) {
			values.addAll(e.getValue());
		}
		return values;
	}
}
