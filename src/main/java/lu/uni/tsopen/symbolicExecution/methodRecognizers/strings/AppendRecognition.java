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

import java.util.ArrayList;
import java.util.List;

import lu.uni.tsopen.symbolicExecution.ContextualValues;
import lu.uni.tsopen.symbolicExecution.SymbolicExecution;
import lu.uni.tsopen.symbolicExecution.symbolicValues.ConstantValue;
import lu.uni.tsopen.symbolicExecution.symbolicValues.MethodRepresentationValue;
import lu.uni.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import lu.uni.tsopen.symbolicExecution.symbolicValues.UnknownValue;
import lu.uni.tsopen.utils.Constants;
import lu.uni.tsopen.utils.Utils;
import soot.SootMethod;
import soot.Value;
import soot.jimple.Constant;
import soot.jimple.StringConstant;

public class AppendRecognition extends StringMethodsRecognitionHandler {

	public AppendRecognition(StringMethodsRecognitionHandler next, SymbolicExecution se) {
		super(next, se);
	}

	@Override
	public List<SymbolicValue> processStringMethod(SootMethod method, Value base, List<Value> args) {
		List<SymbolicValue> values = null;
		List<SymbolicValue> results = new ArrayList<SymbolicValue>();
		ContextualValues contextualValuesOfBase = null;
		if(method.getName().equals(Constants.APPEND)) {
			contextualValuesOfBase = this.se.getContext().get(base);
			if(contextualValuesOfBase == null) {
				results.addAll(this.computeValue(new UnknownValue(this.se), args, base, method));
			}else {
				values = contextualValuesOfBase.getLastCoherentValues(null);
				if(values != null) {
					for(SymbolicValue sv : values) {
						results.addAll(this.computeValue(sv, args, base, method));
					}
				}
			}
			for(SymbolicValue sv : results) {
				Utils.propagateTags(base, sv, this.se);
			}
			return results;
		}
		return null;
	}

	private List<SymbolicValue> computeValue(SymbolicValue symVal, List<Value> args, Value base, SootMethod method) {
		List<SymbolicValue> results = new ArrayList<SymbolicValue>();
		Value effectiveArg = args.get(0);
		ContextualValues contextualValuesOfBase = null;
		List<SymbolicValue> values = null;
		SymbolicValue object = null;
		if(effectiveArg instanceof Constant) {
			if(symVal.isConstant()) {
				object = new ConstantValue(StringConstant.v(String.format("%s%s", symVal, effectiveArg)), this.se);
			}else {
				object = new MethodRepresentationValue(base, args, method, this.se);
			}
			this.addResult(results, object);
		}else {
			contextualValuesOfBase = this.se.getContext().get(effectiveArg);
			if(contextualValuesOfBase == null) {
				if(symVal.isConstant()) {
					object = new ConstantValue(StringConstant.v(String.format("%s%s", symVal, Constants.UNKNOWN_STRING)), this.se);
				}else {
					object = new MethodRepresentationValue(base, args, method, this.se);
				}
				this.addResult(results, object);
			}else {
				values = contextualValuesOfBase.getLastCoherentValues(null);
				if(values != null) {
					for(SymbolicValue sv : values) {
						if(symVal.isConstant() && sv.isConstant()) {
							object = new ConstantValue(StringConstant.v(String.format("%s%s", symVal, sv)), this.se);
						}else {
							object = new MethodRepresentationValue(base, args, method, this.se);
						}
						this.addResult(results, object);
					}
				}
			}
		}
		for(SymbolicValue sv : results) {
			for(Value arg : args) {
				Utils.propagateTags(arg, sv, this.se);
			}
		}
		return results;
	}
}
