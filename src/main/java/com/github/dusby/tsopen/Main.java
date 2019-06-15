package com.github.dusby.tsopen;

import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.profiler.Profiler;

import com.github.dusby.tsopen.pathPredicateRecovery.PathPredicateRecoverer;
import com.github.dusby.tsopen.pathPredicateRecovery.SimpleBlockPredicateExtractioner;
import com.github.dusby.tsopen.symbolicExecution.ContextualValues;
import com.github.dusby.tsopen.symbolicExecution.SymbolicExecutioner;

import soot.Scene;
import soot.SootMethod;
import soot.Value;
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;

public class Main {
	
	private static Logger logger = LoggerFactory.getLogger(Main.class);
	private static Profiler mainProfiler = new Profiler(Main.class.getName());

	public static void main(String[] args) {
		CommandLineOptions options = new CommandLineOptions(args);
		InfoflowAndroidConfiguration ifac = new InfoflowAndroidConfiguration();
		SetupApplication sa = null;
		InfoflowCFG icfg = null;
		SootMethod dummyMainMethod = null;
		SimpleBlockPredicateExtractioner sbpe = null;
		PathPredicateRecoverer ppr = null;
		SymbolicExecutioner se = null;

		mainProfiler.start("CallGraph");
		ifac.getAnalysisFileConfig().setAndroidPlatformDir(options.getPlatforms());
		ifac.getAnalysisFileConfig().setTargetAPKFile(options.getFile());

		sa = new SetupApplication(ifac);
		sa.constructCallgraph();
		icfg = new InfoflowCFG();
		
		mainProfiler.stop();
		logger.info("CallGraph construction : {} ms", TimeUnit.MILLISECONDS.convert(mainProfiler.elapsedTime(), TimeUnit.NANOSECONDS));
		logger.info("CallGraph has {} edges", Scene.v().getCallGraph().size());
		
		dummyMainMethod = sa.getDummyMainMethod();
		
		sbpe = new SimpleBlockPredicateExtractioner(icfg, dummyMainMethod);
		sbpe.traverse();
		
		ppr = new PathPredicateRecoverer(icfg, sbpe, dummyMainMethod, options.hasExceptions());
		ppr.traverse();
		
		se = new SymbolicExecutioner(icfg, dummyMainMethod);
		se.traverse();

		for(Entry<Value, ContextualValues> e : se.getContext().entrySet()) {
			logger.debug("{}", e.getKey());
			logger.debug("{}", e.getValue());
			logger.debug("==========");
		}
	}
}