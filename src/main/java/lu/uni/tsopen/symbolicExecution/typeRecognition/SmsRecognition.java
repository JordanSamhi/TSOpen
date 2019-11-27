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
import lu.uni.tsopen.symbolicExecution.methodRecognizers.sms.CreateFromPduRecognition;
import lu.uni.tsopen.symbolicExecution.methodRecognizers.sms.SmsMethodsRecognitionHandler;
import lu.uni.tsopen.symbolicExecution.symbolicValues.ObjectValue;
import lu.uni.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import lu.uni.tsopen.utils.Constants;
import soot.SootMethod;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;

public class SmsRecognition extends TypeRecognitionHandler {

	private SmsMethodsRecognitionHandler smrh;

	public SmsRecognition(TypeRecognitionHandler next, SymbolicExecution se, InfoflowCFG icfg) {
		super(next, se, icfg);
		this.authorizedTypes.add(Constants.ANDROID_TELEPHONY_SMSMESSAGE);
		this.smrh = new CreateFromPduRecognition(null, se);
	}

	@Override
	public void handleConstructorTag(List<Value> args, ObjectValue object) {}

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
			this.smrh.recognizeSmsMethod(method, object);
			results.add(new Pair<Value, SymbolicValue>(leftOp, object));
		}
		return results;
	}

	@Override
	public void handleInvokeTag(List<Value> args, Value base, SymbolicValue object, SootMethod method) {}
}
