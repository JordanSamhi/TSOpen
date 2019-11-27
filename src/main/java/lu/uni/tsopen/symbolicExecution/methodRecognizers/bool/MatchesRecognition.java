package lu.uni.tsopen.symbolicExecution.methodRecognizers.bool;

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
import lu.uni.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import lu.uni.tsopen.utils.Constants;
import lu.uni.tsopen.utils.Utils;
import soot.SootMethod;
import soot.Value;
import soot.jimple.Constant;
import soot.tagkit.StringConstantValueTag;

public class MatchesRecognition extends BooleanMethodsRecognitionHandler {

	public MatchesRecognition(BooleanMethodsRecognitionHandler next, SymbolicExecution se) {
		super(next, se);
	}

	@Override
	public boolean processBooleanMethod(SootMethod method, Value base, SymbolicValue sv, List<Value> args) {
		Value firstArg = null;
		String methodName = method.getName();
		if(methodName.equals(Constants.MATCHES)) {
			if(args.size() > 0) {
				firstArg = args.get(0);
				if(Utils.containsTag(base, Constants.SMS_BODY_TAG, this.se)
						|| Utils.containsTag(base, Constants.SMS_SENDER_TAG, this.se)
						|| Utils.containsTag(base, Constants.NOW_TAG, this.se)) {
					if(firstArg instanceof Constant) {
						sv.addTag(new StringConstantValueTag(Constants.SUSPICIOUS));
						return true;
					}
				}
			}
		}
		return false;
	}

}
