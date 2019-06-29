package com.github.dusby.tsopen;

import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.profiler.Profiler;

import com.github.dusby.tsopen.logicBombs.PotentialLogicBombsRecovery;
import com.github.dusby.tsopen.pathPredicateRecovery.PathPredicateRecovery;
import com.github.dusby.tsopen.pathPredicateRecovery.SimpleBlockPredicateExtraction;
import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import com.github.dusby.tsopen.utils.TimeOut;

import soot.SootMethod;
import soot.jimple.IfStmt;
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;

public class Main {

	private static Logger logger = LoggerFactory.getLogger(Main.class);
	private static Profiler mainProfiler = new Profiler(Main.class.getName());

	public static void main(String[] args) {
		CommandLineOptions options = new CommandLineOptions(args);
		InfoflowAndroidConfiguration ifac = new InfoflowAndroidConfiguration();
		ifac.setIgnoreFlowsInSystemPackages(false);
		SetupApplication sa = null;
		InfoflowCFG icfg = null;
		SootMethod dummyMainMethod = null;
		SimpleBlockPredicateExtraction sbpe = null;
		PathPredicateRecovery ppr = null;
		SymbolicExecution se = null;
		PotentialLogicBombsRecovery plbr = null;
		Thread sbpeThread = null,
				pprThread = null,
				seThread = null,
				plbrThread = null;
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

		dummyMainMethod = sa.getDummyMainMethod();

		sbpe = new SimpleBlockPredicateExtraction(icfg, dummyMainMethod);
		ppr = new PathPredicateRecovery(icfg, sbpe, dummyMainMethod, options.hasExceptions());
		se = new SymbolicExecution(icfg, dummyMainMethod);
		plbr = new PotentialLogicBombsRecovery(sbpe, se, ppr);

		sbpeThread = new Thread(sbpe, "sbpe");
		pprThread = new Thread(ppr, "pprr");
		seThread = new Thread(se, "syex");
		plbrThread = new Thread(plbr, "plbr");

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

		mainProfiler.start("plbr");
		plbrThread.start();

		try {
			plbrThread.join();
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
		}
		mainProfiler.stop();
		logger.info("Potential Logic Bombs Recovery : {} ms", TimeUnit.MILLISECONDS.convert(mainProfiler.elapsedTime(), TimeUnit.NANOSECONDS));

		logger.info("-------------------------------------------------------------------");

		if(plbr.hasPotentialLogicBombs()) {
			logger.info("Potential Logic bombs found : ");
			for(Entry<IfStmt, List<SymbolicValue>> e : plbr.getPotentialLogicBombs().entrySet()) {
				logger.info("- {}", e.getKey());
				for(SymbolicValue sv : e.getValue()) {
					logger.info("-- {} || {}", sv, sv.getValue());
				}
			}
		}else {
			logger.info("No logic bomb found");
		}

		timeOut.cancel();
	}
}