package lu.uni.tsopen.symbolicExecution.methodRecognizers.location;

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

import lu.uni.tsopen.symbolicExecution.ContextualValues;
import lu.uni.tsopen.symbolicExecution.SymbolicExecution;
import lu.uni.tsopen.symbolicExecution.symbolicValues.MethodRepresentationValue;
import lu.uni.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import lu.uni.tsopen.utils.Constants;
import lu.uni.tsopen.utils.Utils;
import soot.SootMethod;
import soot.Value;
import soot.tagkit.StringConstantValueTag;

public class DistanceBetweenRecognition extends LocationMethodsRecognitionHandler {

	public DistanceBetweenRecognition(LocationMethodsRecognitionHandler next, SymbolicExecution se) {
		super(next, se);
	}

	@Override
	public boolean processLocationMethod(SootMethod method, SymbolicValue sv) {
		MethodRepresentationValue mrv = null;
		Value lastArg = null;
		List<Value> args = null;
		ContextualValues contextualValues = null;
		List<SymbolicValue> values = null;

		if(method.getName().equals(Constants.DISTANCE_BETWEEN)) {
			if(sv instanceof MethodRepresentationValue) {
				mrv = (MethodRepresentationValue) sv;
				args = mrv.getArgs();
				for(Value arg : args) {
					if(Utils.containsTag(arg, Constants.LATITUDE_TAG, this.se)
							|| Utils.containsTag(arg, Constants.LONGITUDE_TAG, this.se)) {
						lastArg = args.get(args.size() - 1);
						contextualValues = this.se.getContextualValues(lastArg);
						if(contextualValues != null) {
							values = contextualValues.getLastCoherentValues(null);
							if(values != null) {
								for(SymbolicValue symval : values) {
									symval.addTag(new StringConstantValueTag(Constants.SUSPICIOUS));
								}
							}
						}
					}
				}
			}
			return true;
		}
		return false;
	}

}
