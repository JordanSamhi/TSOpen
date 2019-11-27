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

import java.util.List;

import org.javatuples.Pair;

import lu.uni.tsopen.symbolicExecution.symbolicValues.ObjectValue;
import lu.uni.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.ReturnStmt;

public interface TypeRecognition {
	public List<Pair<Value, SymbolicValue>> recognizeType(Unit node);
	public List<Pair<Value, SymbolicValue>> processDefinitionStmt(DefinitionStmt defUnit);
	public List<Pair<Value, SymbolicValue>> processInvokeStmt(InvokeStmt invUnit);
	public void handleConstructor(InvokeExpr invExprUnit, Value base, List<Pair<Value, SymbolicValue>> results);
	public void handleConstructorTag(List<Value> args, ObjectValue object);
	public List<Pair<Value, SymbolicValue>> handleDefinitionStmt(DefinitionStmt defUnit);
	public List<Pair<Value, SymbolicValue>> processReturnStmt(ReturnStmt node);
	public abstract void handleInvokeTag(List<Value> args, Value base, SymbolicValue object, SootMethod method);
}