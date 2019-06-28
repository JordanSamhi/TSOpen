package com.github.dusby.tsopen.pathPredicateRecovery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Literal;

import com.github.dusby.tsopen.utils.Edge;
import com.github.dusby.tsopen.utils.ICFGForwardTraversal;
import com.github.dusby.tsopen.utils.Utils;

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

	public SimpleBlockPredicateExtraction(InfoflowCFG icfg, SootMethod mainMethod) {
		super(icfg, "Simple Block Predicate Extraction", mainMethod);
		this.literalToCondition = new HashMap<Literal, IfStmt>();
		this.annotatedEdges = new ArrayList<Edge>();
		this.formulaFactory = new FormulaFactory();
		this.conditions = new ArrayList<IfStmt>();
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

		if(!Utils.isCaughtException(successor)) {
			if(node instanceof IfStmt) {
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

	@Override
	protected void processNeighbor(Unit node, Unit neighbour) {
		this.annotateEdgeWithSimplePredicate(node, neighbour);
	}

	@Override
	protected void processNodeAfterNeighbors(Unit node) {}

	@Override
	protected void processNodeBeforeNeighbors(Unit node) {}
}
