package com.github.dusby.tsopen;

import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.profiler.Profiler;
import org.slf4j.profiler.StopWatch;

import com.github.dusby.tsopen.logicBombs.PotentialLogicBombsRecovery;
import com.github.dusby.tsopen.pathPredicateRecovery.PathPredicateRecovery;
import com.github.dusby.tsopen.pathPredicateRecovery.SimpleBlockPredicateExtraction;
import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import com.github.dusby.tsopen.utils.TimeOut;
import com.github.dusby.tsopen.utils.Utils;

import soot.SootMethod;
import soot.jimple.IfStmt;
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;

public class Main {

	private static Logger logger = LoggerFactory.getLogger(Main.class);
	private static Profiler mainProfiler = new Profiler(Main.class.getName());

	public static void main(String[] args) {
		StopWatch stopWatchCG = new StopWatch("cg"),
				stopWatchSBPE = new StopWatch("sbpe"),
				stopWatchPPR = new StopWatch("ppr"),
				stopWatchSE = new StopWatch("se"),
				stopWatchPLBR = new StopWatch("plbr");
		mainProfiler.start("overall");
		CommandLineOptions options = new CommandLineOptions(args);
		InfoflowAndroidConfiguration ifac = new InfoflowAndroidConfiguration();
		ifac.setIgnoreFlowsInSystemPackages(false);
		SetupApplication sa = null;
		InfoflowCFG icfg = null;
		String fileName = options.getFile();
		SootMethod dummyMainMethod = null;
		SimpleBlockPredicateExtraction sbpe = null;
		PathPredicateRecovery ppr = null;
		SymbolicExecution se = null;
		PotentialLogicBombsRecovery plbr = null;
		Thread sbpeThread = null,
				pprThread = null,
				seThread = null,
				plbrThread = null;
		TimeOut timeOut = new TimeOut(options.getTimeout());
		timeOut.trigger();

		logger.info(String.format("%-35s : %s", "File", fileName));
		logger.info(String.format("%-35s : %3s %s", "Timeout", timeOut.getTimeout(), "mins"));

		stopWatchCG.start("CallGraph");
		ifac.getAnalysisFileConfig().setAndroidPlatformDir(options.getPlatforms());
		ifac.getAnalysisFileConfig().setTargetAPKFile(fileName);

		sa = new SetupApplication(ifac);
		sa.constructCallgraph();
		icfg = new InfoflowCFG();
		stopWatchCG.stop();
		logger.info(String.format("%-35s : %s", "CallGraph construction", Utils.getFormattedTime(stopWatchCG.elapsedTime())));

		dummyMainMethod = sa.getDummyMainMethod();

		sbpe = new SimpleBlockPredicateExtraction(icfg, dummyMainMethod);
		ppr = new PathPredicateRecovery(icfg, sbpe, dummyMainMethod, options.hasExceptions());
		se = new SymbolicExecution(icfg, dummyMainMethod);
		plbr = new PotentialLogicBombsRecovery(sbpe, se, ppr);

		sbpeThread = new Thread(sbpe, "sbpe");
		pprThread = new Thread(ppr, "pprr");
		seThread = new Thread(se, "syex");
		plbrThread = new Thread(plbr, "plbr");

		stopWatchSBPE.start("sbpe");
		sbpeThread.start();
		stopWatchSE.start("se");
		seThread.start();

		try {
			sbpeThread.join();
			stopWatchSBPE.stop();
			logger.info(String.format("%-35s : %s", "Simple Block Predicate Extraction", Utils.getFormattedTime(stopWatchSBPE.elapsedTime())));
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
		}

		stopWatchPPR.start("ppr");
		pprThread.start();

		try {
			pprThread.join();
			stopWatchPPR.stop();
			logger.info(String.format("%-35s : %s", "Path Predicate Recovery", Utils.getFormattedTime(stopWatchPPR.elapsedTime())));
			seThread.join();
			stopWatchSE.stop();
			logger.info(String.format("%-35s : %s", "Symbolic Execution", Utils.getFormattedTime(stopWatchSE.elapsedTime())));
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
		}

		stopWatchPLBR.start("plbr");
		plbrThread.start();

		try {
			plbrThread.join();
			stopWatchPLBR.stop();
			logger.info(String.format("%-35s : %s", "Potential Logic Bombs Recovery", Utils.getFormattedTime(stopWatchPLBR.elapsedTime())));
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
		}

		mainProfiler.stop();
		logger.info(String.format("%-35s : %s", "Application Execution Time", Utils.getFormattedTime(mainProfiler.elapsedTime())));

		printResults(plbr, icfg);
		timeOut.cancel();
	}

	private static void printResults(PotentialLogicBombsRecovery plbr, InfoflowCFG icfg) {
		SootMethod ifMethod = null;
		if(plbr.hasPotentialLogicBombs()) {
			System.out.println("\nPotential Logic Bombs found : ");
			System.out.println("----------------------------------------------------------------");
			for(Entry<IfStmt, List<SymbolicValue>> e : plbr.getPotentialLogicBombs().entrySet()) {
				ifMethod = icfg.getMethodOf(e.getKey());
				System.out.println(String.format("- %-10s : if %s", "Statement", e.getKey().getCondition()));
				System.out.println(String.format("- %-10s : %s", "Class", ifMethod.getDeclaringClass()));
				System.out.println(String.format("- %-10s : %s", "Method", ifMethod.getName()));
				for(SymbolicValue sv : e.getValue()) {
					System.out.println(String.format("- %-10s : %s (%s)", "Predicate", sv.getValue(), sv));
				}
				System.out.println("----------------------------------------------------------------");
			}
		}else {
			System.out.println("No Logic Bomb found");
		}
	}
}