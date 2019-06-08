package com.github.dusby.tsopen.utils;

import org.logicng.formulas.Formula;

import soot.Unit;

/**
 * This class represents an edge between two unit in a CFG/ICFG.
 * It can be annotated with a predicate.
 * @author Jordan Samhi
 *
 */
public class Edge {

	private final Unit source;
	private final Unit target;
	private Formula predicate;

	public Edge(Unit s, Unit t) {
		this.source = s;
		this.target = t;
		this.predicate = null;
	}

	public Unit getSource() {
		return source;
	}

	public Unit getTarget() {
		return target;
	}

	public Formula getPredicate() {
		return predicate;
	}

	public void setPredicate(Formula predicate) {
		this.predicate = predicate;
	}
	
	@Override
	public String toString() {
		return String.format("(%s) -> (%s)", this.source, this.target);
	}

}
