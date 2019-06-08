package com.github.dusby.tsopen.utils;

import org.logicng.formulas.Formula;

import soot.Unit;

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
