package com.github.dusby.tsopen.symbolicExecution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Literal;

import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValueProvider;
import com.github.dusby.tsopen.utils.Edge;
import com.github.dusby.tsopen.utils.ICFGForwardTraverser;
import com.github.dusby.tsopen.utils.Utils;

import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.ConditionExpr;
import soot.jimple.IfStmt;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;

/**
 * 
 * This is the class implementing two of the first phases of TriggerScope.
 * Namely the symbolic value modeling and the block predicate extraction.
 * @author Jordan Samhi
 *
 */
public class SymbolicExecutioner extends ICFGForwardTraverser {
	private Map<Value, SymbolicValueProvider> symbolicExecutionResults;
	private Map<Literal, ConditionExpr> literalToCondition = null;
	private Map<Unit, List<Edge>> nodeToEdges;
	private final FormulaFactory formulaFactory;

	public SymbolicExecutioner(InfoflowCFG icfg, SootMethod mainMethod) {
		super(icfg, "Symbolic execution", mainMethod);
		this.symbolicExecutionResults = new HashMap<Value, SymbolicValueProvider>();
		this.literalToCondition = new HashMap<Literal, ConditionExpr>();
		this.nodeToEdges = new HashMap<Unit, List<Edge>>();
		this.formulaFactory = new FormulaFactory();
	}

	/**
	 * Annotate simple predicates on condition's edges
	 * @param node the current node being traversed
	 * @param successor one of the successor of the current node
	 */
	private void predicateAnnotation(Unit node, Unit successor) {
		IfStmt ifStmt = null;
		String condition = null;
		Edge edge = null;
		Literal simplePredicate = null;
		List<Edge> edgesOfNode = this.nodeToEdges.get(node);

		if(!Utils.isCaughtException(successor)) {
			if(node instanceof IfStmt) {
				if(edgesOfNode == null) {
					edgesOfNode = new ArrayList<Edge>();
				}
				edge = new Edge(node, successor);
				edgesOfNode.add(edge);
				this.nodeToEdges.put(node, edgesOfNode);
				ifStmt = (IfStmt) node;
				condition = String.format("([%s] => %s)", ifStmt.hashCode(), ifStmt.getCondition().toString());
				if(successor == ifStmt.getTarget()) {
					simplePredicate = this.formulaFactory.literal(condition, true);
				}else {
					simplePredicate = this.formulaFactory.literal(condition, false);
				}
				this.literalToCondition.put(simplePredicate, (ConditionExpr) ifStmt.getCondition());
				edge.setPredicate(simplePredicate);
			}
		}
	}

	public Map<Value, SymbolicValueProvider> getModelContext() {
		return this.symbolicExecutionResults;
	}

	@Override
	protected void processNeighbor(Unit node, Unit neighbour) {
		this.predicateAnnotation(node, neighbour);
	}

	public Map<Unit, List<Edge>> getNodeToEdges() {
		return nodeToEdges;
	}
}
