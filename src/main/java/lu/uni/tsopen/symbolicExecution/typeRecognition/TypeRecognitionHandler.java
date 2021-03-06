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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lu.uni.tsopen.symbolicExecution.ContextualValues;
import lu.uni.tsopen.symbolicExecution.SymbolicExecution;
import lu.uni.tsopen.symbolicExecution.methodRecognizers.location.DistanceBetweenRecognition;
import lu.uni.tsopen.symbolicExecution.methodRecognizers.location.LocationMethodsRecognitionHandler;
import lu.uni.tsopen.symbolicExecution.symbolicValues.ConstantValue;
import lu.uni.tsopen.symbolicExecution.symbolicValues.MethodRepresentationValue;
import lu.uni.tsopen.symbolicExecution.symbolicValues.ObjectValue;
import lu.uni.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import lu.uni.tsopen.symbolicExecution.symbolicValues.UnknownValue;
import lu.uni.tsopen.utils.Utils;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.Constant;
import soot.jimple.DefinitionStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.ReturnStmt;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;

public abstract class TypeRecognitionHandler implements TypeRecognition {

	private TypeRecognitionHandler next;
	protected SymbolicExecution se;
	protected InfoflowCFG icfg;
	protected List<String> authorizedTypes;
	private LocationMethodsRecognitionHandler lmrh;

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	public TypeRecognitionHandler(TypeRecognitionHandler next, SymbolicExecution se, InfoflowCFG icfg) {
		this.next = next;
		this.se = se;
		this.icfg = icfg;
		this.authorizedTypes = new LinkedList<String>();
		this.lmrh = new DistanceBetweenRecognition(null, se);
	}

	@Override
	public List<Pair<Value, SymbolicValue>> recognizeType(Unit node) {
		List<Pair<Value, SymbolicValue>> result = null;
		if(node instanceof DefinitionStmt) {
			result = this.processDefinitionStmt((DefinitionStmt) node);
		}else if (node instanceof InvokeStmt) {
			result = this.processInvokeStmt((InvokeStmt) node);
		}else if(node instanceof ReturnStmt) {
			result = this.processReturnStmt((ReturnStmt)node);
		}

		if(result != null && !result.isEmpty()) {
			return result;
		}
		if(this.next != null) {
			return this.next.recognizeType(node);
		}
		else {
			return null;
		}
	}

	@Override
	public List<Pair<Value, SymbolicValue>> processReturnStmt(ReturnStmt returnStmt) {
		Collection<Unit> callers = this.icfg.getCallersOf(this.icfg.getMethodOf(returnStmt));
		AssignStmt callerAssign = null;
		Value leftOp = null,
				returnOp = returnStmt.getOp();
		List<SymbolicValue> values = null;
		ContextualValues contextualValues = this.se.getContext().get(returnOp);
		List<Pair<Value, SymbolicValue>> results = new LinkedList<Pair<Value,SymbolicValue>>();
		SymbolicValue object = null;
		SootMethod callerMethod = null;

		for(Unit caller : callers) {
			callerMethod = this.icfg.getMethodOf(caller);
			if(caller instanceof AssignStmt) {
				callerAssign = (AssignStmt) caller;
				leftOp = callerAssign.getLeftOp();
				if(contextualValues == null) {
					if(returnOp instanceof Constant) {
						object = new ConstantValue((Constant)returnOp, this.se);
					}else {
						object = new UnknownValue(this.se);
					}
					Utils.propagateTags(returnOp, object, this.se);
					results.add(new Pair<Value, SymbolicValue>(leftOp, object));
				}else {
					values = contextualValues.getLastCoherentValues(null);
					if(values != null) {
						for(SymbolicValue sv : values) {
							results.add(new Pair<Value, SymbolicValue>(leftOp, sv));
						}
					}
				}
			}
			if(this.se.isMethodVisited(callerMethod)) {
				this.se.addMethodToWorkList(callerMethod);
			}
		}
		return results;
	}

	@Override
	public List<Pair<Value, SymbolicValue>> processDefinitionStmt(DefinitionStmt defUnit) {
		if(this.isAuthorizedType(defUnit.getLeftOp().getType().toString())) {
			return this.handleDefinitionStmt(defUnit);
		}
		return null;
	}

	@Override
	public List<Pair<Value, SymbolicValue>> processInvokeStmt(InvokeStmt invUnit) {
		Value base = null;
		InvokeExpr invExprUnit = invUnit.getInvokeExpr();
		SootMethod m = invExprUnit.getMethod();
		List<Pair<Value, SymbolicValue>> results = new LinkedList<Pair<Value,SymbolicValue>>();

		if(invExprUnit instanceof InstanceInvokeExpr) {
			base = ((InstanceInvokeExpr) invExprUnit).getBase();
			if(base != null) {
				if(this.isAuthorizedType(base.getType().toString())) {
					if(m.isConstructor()) {
						this.handleConstructor(invExprUnit, base, results);
					}else if(invExprUnit instanceof InstanceInvokeExpr){
						this.handleInvokeStmt(invExprUnit, base, results);
					}
				}
			}
		}else if (invExprUnit instanceof StaticInvokeExpr) {
			this.handleInvokeStmt(invExprUnit, base, results);
		}
		return results;
	}

	protected void handleInvokeStmt(InvokeExpr invExprUnit, Value base, List<Pair<Value, SymbolicValue>> results) {
		SootMethod method = invExprUnit.getMethod();
		List<Value> args = invExprUnit.getArgs();
		SymbolicValue object = new MethodRepresentationValue(base, args, method, this.se);
		this.lmrh.recognizeLocationMethod(method, object);
		this.handleInvokeTag(args, base, object, method);
		results.add(new Pair<Value, SymbolicValue>(base, object));
	}

	@Override
	public abstract void handleInvokeTag(List<Value> args, Value base, SymbolicValue object, SootMethod method);

	@Override
	public void handleConstructor(InvokeExpr invExprUnit, Value base, List<Pair<Value, SymbolicValue>> results) {
		List<Value> args = invExprUnit.getArgs();
		ObjectValue object = new ObjectValue(base.getType(), args, this.se);
		this.handleConstructorTag(args, object);
		results.add(new Pair<Value, SymbolicValue>(base, object));
	}

	protected boolean isAuthorizedType(String type) {
		return this.authorizedTypes.contains(type);
	}

	protected void checkAndProcessContextValues(Value v, List<Pair<Value, SymbolicValue>> results, Value leftOp, Unit node) {
		ContextualValues contextualValues = this.se.getContext().get(v);
		List<SymbolicValue> values = null;
		if(contextualValues == null) {
			results.add(new Pair<Value, SymbolicValue>(leftOp, new UnknownValue(this.se)));
		}else {
			values = contextualValues.getLastCoherentValues(node);
			if(values != null) {
				for(SymbolicValue sv : values) {
					results.add(new Pair<Value, SymbolicValue>(leftOp, sv));
				}
			}
		}
	}
}