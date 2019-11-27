package lu.uni.tsopen.pathPredicateRecovery;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Literal;

import lu.uni.tsopen.graphTraversal.ICFGForwardTraversal;
import lu.uni.tsopen.utils.Edge;
import lu.uni.tsopen.utils.Utils;
import soot.Local;
import soot.RefLikeType;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.IfStmt;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;

/**
 * This class extract the simple block predicates for the
 * future full path predicate recovery
 * @author Jordan Samhi
 *
 */
public class SimpleBlockPredicateExtraction extends ICFGForwardTraversal {

	private Map<Literal, IfStmt> literalToCondition = null;
	private List<IfStmt> conditions;
	private List<Edge> annotatedEdges;
	private final FormulaFactory formulaFactory;
	private List<IfStmt> visitedIfs;
	private Map<SootMethod, Integer> countOfIfByMethod;
	private List<SootMethod> visitedMethods;
	private int countOfObject;
	private int maxIf;

	public SimpleBlockPredicateExtraction(InfoflowCFG icfg, SootMethod mainMethod) {
		super(icfg, "Simple Block Predicate Extraction", mainMethod);
		this.literalToCondition = new HashMap<Literal, IfStmt>();
		this.annotatedEdges = new ArrayList<Edge>();
		this.formulaFactory = new FormulaFactory();
		this.conditions = new ArrayList<IfStmt>();
		this.visitedIfs = new ArrayList<IfStmt>();
		this.countOfIfByMethod = new HashMap<SootMethod, Integer>();
		this.visitedMethods = new ArrayList<SootMethod>();
		this.countOfObject = 0;
		this.maxIf = 0;
	}

	/**
	 * Annotate simple predicates on condition's edges
	 * @param node the current node being traversed
	 * @param successor one of the successor of the current node
	 */
	private void annotateEdgeWithSimplePredicate(Unit node, Unit successor) {
		IfStmt ifStmt = null;
		String condition = null;
		Edge edge = null;
		Literal simplePredicate = null;
		SootMethod method = this.icfg.getMethodOf(node);
		Integer countOfIfByMethod = null;

		if(!Utils.isCaughtException(successor)) {
			if(node instanceof IfStmt && !Utils.isDummy(this.icfg.getMethodOf(node))) {
				edge = new Edge(node, successor);
				this.annotatedEdges.add(edge);
				ifStmt = (IfStmt) node;
				condition = String.format("([%s] => %s)", ifStmt.hashCode(), ifStmt.getCondition().toString());
				if(successor == ifStmt.getTarget()) {
					simplePredicate = this.formulaFactory.literal(condition, true);
				}else {
					simplePredicate = this.formulaFactory.literal(condition, false);
				}
				this.literalToCondition.put(simplePredicate, ifStmt);
				if(!this.conditions.contains(ifStmt)) {
					this.conditions.add(ifStmt);
				}
				edge.setPredicate(simplePredicate);
				if(!this.visitedIfs.contains(ifStmt)) {
					this.visitedIfs.add(ifStmt);
					countOfIfByMethod = this.countOfIfByMethod.get(method);
					if(countOfIfByMethod == null) {
						this.countOfIfByMethod.put(method, 1);
					}else {
						countOfIfByMethod += 1;
						this.countOfIfByMethod.put(method, countOfIfByMethod);
						if (countOfIfByMethod > this.maxIf) {
							this.maxIf = countOfIfByMethod;
						}
					}
				}
			}
			if(!this.visitedMethods.contains(method)) {
				this.visitedMethods.add(method);
				for(Local l : method.retrieveActiveBody().getLocals()) {
					if(l.getType() instanceof RefLikeType) {
						this.countOfObject += 1;
					}
				}
			}
		}
	}

	/**
	 * Return the edge corresponding to the units in
	 * the given order
	 * @param source the source node of the edge
	 * @param target the target node of the edge
	 * @return the edge corresponding to the nodes
	 */
	public Edge getAnnotatedEdge(Unit source, Unit target) {
		for(Edge edge : this.annotatedEdges) {
			if(edge.correspondsTo(source, target)) {
				return edge;
			}
		}
		return null;
	}

	public IfStmt getCondtionFromLiteral(Literal l) {
		if(this.literalToCondition.containsKey(l)) {
			return this.literalToCondition.get(l);
		}
		return null;
	}

	public List<IfStmt> getConditions(){
		return this.conditions;
	}

	public int getIfCount() {
		return this.visitedIfs.size();
	}

	public int getIfDepthInMethods() {
		return this.maxIf;
	}

	@Override
	protected void processNeighbor(Unit node, Unit neighbour) {
		this.annotateEdgeWithSimplePredicate(node, neighbour);
	}

	@Override
	protected void processNodeAfterNeighbors(Unit node) {}

	@Override
	protected void processNodeBeforeNeighbors(Unit node) {}

	public int getCountOfObject() {
		return this.countOfObject;
	}
}
