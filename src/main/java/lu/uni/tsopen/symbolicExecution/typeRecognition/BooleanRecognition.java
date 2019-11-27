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
import lu.uni.tsopen.symbolicExecution.methodRecognizers.bool.AfterRecognition;
import lu.uni.tsopen.symbolicExecution.methodRecognizers.bool.BeforeRecognition;
import lu.uni.tsopen.symbolicExecution.methodRecognizers.bool.BooleanMethodsRecognitionHandler;
import lu.uni.tsopen.symbolicExecution.methodRecognizers.bool.ContainsRecognition;
import lu.uni.tsopen.symbolicExecution.methodRecognizers.bool.EndsWithRecognition;
import lu.uni.tsopen.symbolicExecution.methodRecognizers.bool.EqualsRecognition;
import lu.uni.tsopen.symbolicExecution.methodRecognizers.bool.MatchesRecognition;
import lu.uni.tsopen.symbolicExecution.methodRecognizers.bool.StartsWithRecognition;
import lu.uni.tsopen.symbolicExecution.symbolicValues.FieldValue;
import lu.uni.tsopen.symbolicExecution.symbolicValues.MethodRepresentationValue;
import lu.uni.tsopen.symbolicExecution.symbolicValues.ObjectValue;
import lu.uni.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import lu.uni.tsopen.utils.Constants;
import lu.uni.tsopen.utils.Utils;
import soot.SootMethod;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;

public class BooleanRecognition extends TypeRecognitionHandler {

	private BooleanMethodsRecognitionHandler bmrh;

	public BooleanRecognition(TypeRecognitionHandler next, SymbolicExecution se, InfoflowCFG icfg) {
		super(next, se, icfg);
		this.authorizedTypes.add(Constants.BOOLEAN);
		this.bmrh = new AfterRecognition(null, se);
		this.bmrh = new BeforeRecognition(this.bmrh, se);
		this.bmrh = new EqualsRecognition(this.bmrh, se);
		this.bmrh = new ContainsRecognition(this.bmrh, se);
		this.bmrh = new StartsWithRecognition(this.bmrh, se);
		this.bmrh = new EndsWithRecognition(this.bmrh, se);
		this.bmrh = new MatchesRecognition(this.bmrh, se);
	}

	@Override
	public void handleConstructorTag(List<Value> args, ObjectValue object) {}

	@Override
	public List<Pair<Value, SymbolicValue>> handleDefinitionStmt(DefinitionStmt defUnit) {
		Value leftOp = defUnit.getLeftOp(),
				rightOp = defUnit.getRightOp(),
				base = null;
		List<Pair<Value, SymbolicValue>> results = new LinkedList<Pair<Value,SymbolicValue>>();
		InstanceInvokeExpr rightOpInvExpr = null;
		InstanceFieldRef field = null;
		SootMethod method = null;
		List<Value> args = null;
		SymbolicValue object = null;

		if(rightOp instanceof InstanceInvokeExpr) {
			rightOpInvExpr = (InstanceInvokeExpr) rightOp;
			method = rightOpInvExpr.getMethod();
			args = rightOpInvExpr.getArgs();
			base = rightOpInvExpr.getBase();
			object = new MethodRepresentationValue(base, args, method, this.se);
			this.bmrh.recognizeBooleanMethod(method, base, object, args);
		}else if(rightOp instanceof InstanceFieldRef){
			field = (InstanceFieldRef) rightOp;
			base = field.getBase();
			object = new FieldValue(base, field.getField().getName(), this.se);
			Utils.propagateTags(rightOp, object, this.se);
		}else {
			return results;
		}
		results.add(new Pair<Value, SymbolicValue>(leftOp, object));
		return results;
	}

	@Override
	public void handleInvokeTag(List<Value> args, Value base, SymbolicValue object, SootMethod method) {}
}
