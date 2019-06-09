package com.github.dusby.tsopen.pathPredicateRecovery;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.profiler.Profiler;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecutioner;

import soot.SootMethod;
import soot.Unit;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;

public class PathPredicateRecoverer {
	
	private final InfoflowCFG icfg;
	private final SymbolicExecutioner se;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private Profiler profiler = new Profiler(this.getClass().getName());
	private LinkedList<SootMethod> methodWorkList;
	private List<SootMethod> visitedMethods;

	public PathPredicateRecoverer(InfoflowCFG icfg, SymbolicExecutioner se) {
		this.icfg = icfg;
		this.se = se;
		this.methodWorkList = new LinkedList<SootMethod>();
		this.visitedMethods = new LinkedList<SootMethod>();
	}

	public void recover(Unit node) {
		profiler.start("execute");
		SootMethod methodToAnalyze = null;
		Unit entryPoint = null;
		while(!this.methodWorkList.isEmpty()) {
			methodToAnalyze = this.methodWorkList.removeFirst();
			this.visitedMethods.add(methodToAnalyze);
			entryPoint = this.icfg.getStartPointsOf(methodToAnalyze).iterator().next();
			this.recover(entryPoint);
		}
		profiler.stop();
		this.logger.info("Symbolic execution : {} ms", TimeUnit.MILLISECONDS.convert(profiler.elapsedTime(), TimeUnit.NANOSECONDS));
	}

}
