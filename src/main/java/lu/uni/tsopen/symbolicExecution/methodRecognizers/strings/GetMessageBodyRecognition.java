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
import lu.uni.tsopen.symbolicExecution.symbolicValues.MethodRepresentationValue;
import lu.uni.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import lu.uni.tsopen.utils.Constants;
import soot.SootMethod;
import soot.Value;
import soot.tagkit.StringConstantValueTag;

public class GetMessageBodyRecognition extends StringMethodsRecognitionHandler {

	public GetMessageBodyRecognition(StringMethodsRecognitionHandler next, SymbolicExecution se) {
		super(next, se);
	}

	@Override
	public List<SymbolicValue> processStringMethod(SootMethod method, Value base, List<Value> args) {
		List<SymbolicValue> results = new ArrayList<SymbolicValue>();
		MethodRepresentationValue mrv = new MethodRepresentationValue(base, args, method, this.se);
		if(method.getName().equals(Constants.GET_MESSAGE_BODY) || method.getName().equals(Constants.GET_DISPLAY_MESSAGE_BODY)) {
			mrv.addTag(new StringConstantValueTag(Constants.SMS_BODY_TAG));
			this.addResult(results, mrv);
		}
		return results;
	}
}
