package lu.uni.tsopen.symbolicExecution.methodRecognizers.numeric;

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
import lu.uni.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import lu.uni.tsopen.utils.Utils;
import soot.SootMethod;
import soot.Value;
import soot.tagkit.StringConstantValueTag;

public abstract class NumericMethodsRecognitionHandler implements NumericMethodsRecognition {

	private NumericMethodsRecognitionHandler next;
	protected SymbolicExecution se;

	public NumericMethodsRecognitionHandler(NumericMethodsRecognitionHandler next, SymbolicExecution se) {
		this.next = next;
		this.se = se;
	}

	@Override
	public boolean recognizeNumericMethod(SootMethod method, Value base, SymbolicValue sv) {
		boolean recognized = this.processNumericMethod(method, base, sv);

		if(recognized) {
			return recognized;
		}
		if(this.next != null) {
			return this.next.recognizeNumericMethod(method, base, sv);
		}
		else {
			return false;
		}
	}

	@Override
	public boolean genericProcessNumericMethod(SootMethod method, Value base, SymbolicValue sv,
			String className, String methodName, String containedTag, String addedTag) {
		if(method.getDeclaringClass().getName().equals(className) && method.getName().equals(methodName)) {
			if(this.isTagHandled(containedTag, addedTag, base, sv)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isTagHandled(String containedTag, String addedTag, Value base, SymbolicValue sv) {
		if(Utils.containsTag(base, containedTag, this.se)) {
			sv.addTag(new StringConstantValueTag(addedTag));
			return true;
		}
		return false;
	}
}
