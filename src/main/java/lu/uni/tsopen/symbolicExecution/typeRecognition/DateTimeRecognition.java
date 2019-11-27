package lu.uni.tsopen.symbolicExecution.typeRecognition;

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

import java.util.LinkedList;
import java.util.List;

import org.javatuples.Pair;

import lu.uni.tsopen.symbolicExecution.SymbolicExecution;
import lu.uni.tsopen.symbolicExecution.methodRecognizers.dateTime.DateTimeMethodsRecognitionHandler;
import lu.uni.tsopen.symbolicExecution.methodRecognizers.dateTime.GetInstanceRecognition;
import lu.uni.tsopen.symbolicExecution.methodRecognizers.dateTime.NowRecognition;
import lu.uni.tsopen.symbolicExecution.methodRecognizers.dateTime.SetToNowRecognition;
import lu.uni.tsopen.symbolicExecution.symbolicValues.ObjectValue;
import lu.uni.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import lu.uni.tsopen.utils.Constants;
import soot.SootMethod;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;
import soot.tagkit.StringConstantValueTag;

public class DateTimeRecognition extends TypeRecognitionHandler {

	private DateTimeMethodsRecognitionHandler dtmrh;

	public DateTimeRecognition(TypeRecognitionHandler next, SymbolicExecution se, InfoflowCFG icfg) {
		super(next, se, icfg);
		this.authorizedTypes.add(Constants.JAVA_UTIL_DATE);
		this.authorizedTypes.add(Constants.JAVA_UTIL_CALENDAR);
		this.authorizedTypes.add(Constants.JAVA_UTIL_GREGORIAN_CALENDAR);
		this.authorizedTypes.add(Constants.JAVA_TIME_LOCAL_DATE_TIME);
		this.authorizedTypes.add(Constants.JAVA_TIME_LOCAL_DATE);
		this.authorizedTypes.add(Constants.JAVA_TEXT_SIMPLE_DATE_FORMAT);
		this.authorizedTypes.add(Constants.ANDROID_TEXT_FORMAT_TIME);
		this.dtmrh = new GetInstanceRecognition(null, se);
		this.dtmrh = new NowRecognition(this.dtmrh, se);
		this.dtmrh = new SetToNowRecognition(this.dtmrh, se);
	}

	@Override
	public List<Pair<Value, SymbolicValue>> handleDefinitionStmt(DefinitionStmt defUnit) {
		Value leftOp = defUnit.getLeftOp(),
				rightOp = defUnit.getRightOp();
		List<Pair<Value, SymbolicValue>> results = new LinkedList<Pair<Value,SymbolicValue>>();
		StaticInvokeExpr rightOpStaticInvokeExpr = null;
		SootMethod method = null;
		List<Value> args = null;
		ObjectValue object = null;

		if(rightOp instanceof StaticInvokeExpr) {
			rightOpStaticInvokeExpr = (StaticInvokeExpr) rightOp;
			method = rightOpStaticInvokeExpr.getMethod();
			args = rightOpStaticInvokeExpr.getArgs();
			object = new ObjectValue(method.getDeclaringClass().getType(), args, this.se);
			this.dtmrh.recognizeDateTimeMethod(method, object);
			results.add(new Pair<Value, SymbolicValue>(leftOp, object));
		}
		return results;
	}

	@Override
	public void handleConstructorTag(List<Value> args, ObjectValue object) {
		if(args.size() == 0) {
			object.addTag(new StringConstantValueTag(Constants.NOW_TAG));
		}
	}

	@Override
	public void handleInvokeTag(List<Value> args, Value base, SymbolicValue object, SootMethod method) {
		this.dtmrh.recognizeDateTimeMethod(method, object);
	}
}
