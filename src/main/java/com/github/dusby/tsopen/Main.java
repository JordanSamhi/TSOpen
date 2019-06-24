package com.github.dusby.tsopen;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.profiler.Profiler;

import com.github.dusby.tsopen.pathPredicateRecovery.PathPredicateRecovery;
import com.github.dusby.tsopen.pathPredicateRecovery.SimpleBlockPredicateExtraction;
import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;
import com.github.dusby.tsopen.utils.TimeOut;

import soot.Scene;
import soot.SootMethod;
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
		SimpleBlockPredicateExtraction sbpe = null;
		PathPredicateRecovery ppr = null;
		SymbolicExecution se = null;
		Thread sbpeThread = null,
				pprThread = null,
				seThread = null;
		TimeOut timeOut = new TimeOut();
		timeOut.trigger(options.getTimeout());

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

		sbpe = new SimpleBlockPredicateExtraction(icfg, dummyMainMethod);
		ppr = new PathPredicateRecovery(icfg, sbpe, dummyMainMethod, options.hasExceptions());
		se = new SymbolicExecution(icfg, dummyMainMethod);

		sbpeThread = new Thread(sbpe, "Symbolic Block Predicate Extraction");
		pprThread = new Thread(ppr, "Path Predicate Recovery");
		seThread = new Thread(se, "Symbolic Execution");

		sbpeThread.start();
		seThread.start();

		try {
			sbpeThread.join();
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
		}

		pprThread.start();

		try {
			pprThread.join();
			seThread.join();
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
		}
		timeOut.cancel();
	}
}