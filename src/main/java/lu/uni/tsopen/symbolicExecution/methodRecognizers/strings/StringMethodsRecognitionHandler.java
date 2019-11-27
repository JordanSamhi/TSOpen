package lu.uni.tsopen.symbolicExecution.methodRecognizers.strings;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lu.uni.tsopen.symbolicExecution.ContextualValues;
import lu.uni.tsopen.symbolicExecution.SymbolicExecution;
import lu.uni.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import lu.uni.tsopen.symbolicExecution.symbolicValues.UnknownValue;
import soot.SootMethod;
import soot.Value;

public abstract class StringMethodsRecognitionHandler implements StringMethodsRecognition {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	private StringMethodsRecognitionHandler next;
	protected SymbolicExecution se;

	public StringMethodsRecognitionHandler(StringMethodsRecognitionHandler next, SymbolicExecution se) {
		this.next = next;
		this.se = se;
	}

	@Override
	public List<SymbolicValue> recognizeStringMethod(SootMethod method, Value base, List<Value> args) {
		List<SymbolicValue> result = this.processStringMethod(method, base, args);

		if(result != null && !result.isEmpty()) {
			return result;
		}
		if(this.next != null) {
			return this.next.recognizeStringMethod(method, base, args);
		}
		else {
			return null;
		}
	}

	protected void addSimpleResult(Value v, List<SymbolicValue> results) {
		ContextualValues contextualValues = this.se.getContext().get(v);
		List<SymbolicValue> values = null;
		if(contextualValues == null) {
			results.add(new UnknownValue(this.se));
		}else {
			values = contextualValues.getLastCoherentValues(null);
			if(values != null) {
				for(SymbolicValue sv : values) {
					this.addResult(results, sv);
				}
			}
		}
	}

	protected void addResult(List<SymbolicValue> results, SymbolicValue object) {
		for(SymbolicValue sv : results) {
			if(sv.toString().equals(object.toString())) {
				return;
			}
		}
		results.add(object);
	}
}