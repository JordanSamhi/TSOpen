package com.github.dusby;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.profiler.Profiler;

import com.github.dusby.symbolicExecution.SymbolicExecutioner;

import soot.Scene;
import soot.SootMethod;
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;

public class Main {
	
//	private static Logger logger = LoggerFactory.getLogger("Main");
//	private static Profiler mainProfiler = new Profiler("Main");

	public static void main(String[] args) {
		CommandLineOptions options = new CommandLineOptions(args);

//		mainProfiler.start("CallGraph");
		InfoflowAndroidConfiguration ifac = new InfoflowAndroidConfiguration();
		ifac.getAnalysisFileConfig().setAndroidPlatformDir(options.getPlatforms());
		ifac.getAnalysisFileConfig().setTargetAPKFile(options.getFile());

		SetupApplication sa = new SetupApplication(ifac);
		sa.constructCallgraph();
		InfoflowCFG icfg = new InfoflowCFG();
//		mainProfiler.stop();
//		logger.info("CallGraph construction : {} ms", TimeUnit.MILLISECONDS.convert(mainProfiler.elapsedTime(), TimeUnit.NANOSECONDS));
//		logger.info("CallGraph has {} edges", Scene.v().getCallGraph().size());
		
		SootMethod dummyMainMethod = sa.getDummyMainMethod();
		SymbolicExecutioner se = new SymbolicExecutioner(icfg, dummyMainMethod);
		se.execute();
	}
}