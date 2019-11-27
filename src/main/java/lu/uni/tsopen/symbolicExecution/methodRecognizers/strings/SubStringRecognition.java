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

import lu.uni.tsopen.symbolicExecution.SymbolicExecution;
import lu.uni.tsopen.symbolicExecution.symbolicValues.ConstantValue;
import lu.uni.tsopen.symbolicExecution.symbolicValues.MethodRepresentationValue;
import lu.uni.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import lu.uni.tsopen.utils.Constants;
import soot.SootMethod;
import soot.Value;
import soot.jimple.IntConstant;
import soot.jimple.StringConstant;

public class SubStringRecognition extends StringMethodsRecognitionHandler {

	public SubStringRecognition(StringMethodsRecognitionHandler next, SymbolicExecution se) {
		super(next, se);
	}

	@Override
	public List<SymbolicValue> processStringMethod(SootMethod method, Value base, List<Value> args) {
		List<SymbolicValue> results = new ArrayList<SymbolicValue>();
		StringConstant baseStr = null;
		Value arg1 = null,
				arg2 = null;
		int v1 = 0,
				v2 = 0;
		SymbolicValue object = null;
		if(method.getName().equals(Constants.SUBSTRING)) {
			if(base instanceof StringConstant) {
				baseStr = (StringConstant) base;
				if(baseStr.value.contains(Constants.UNKNOWN_STRING)){
					object = new MethodRepresentationValue(base, args, method, this.se);
				}else {
					arg1 = args.get(0);
					if(arg1 instanceof IntConstant) {
						v1 = ((IntConstant)arg1).value;
						if(args.size() == 1) {
							object = new ConstantValue(StringConstant.v(baseStr.value.substring(v1)), this.se);
						}else {
							arg2 = args.get(1);
							if(arg2 instanceof IntConstant) {
								v2 = ((IntConstant)arg2).value;
								object = new ConstantValue(StringConstant.v(baseStr.value.substring(v1, v2)), this.se);
							}
						}
					}
				}
			}else {
				object = new MethodRepresentationValue(base, args, method, this.se);
			}
			if(object != null) {
				this.addResult(results, object);
			}
			return results;
		}
		return null;
	}

}
