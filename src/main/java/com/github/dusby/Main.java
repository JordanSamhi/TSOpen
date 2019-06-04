package com.github.dusby;

import com.github.dusby.symbolicExecution.SymbolicExecutioner;

import soot.SootMethod;
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;

public class Main {

	public static void main(String[] args) {
		CommandLineOptions options = new CommandLineOptions(args);
		
		InfoflowAndroidConfiguration ifac = new InfoflowAndroidConfiguration();
		ifac.getAnalysisFileConfig().setAndroidPlatformDir(options.getPlatforms());
		ifac.getAnalysisFileConfig().setTargetAPKFile(options.getFile());
		
		SetupApplication sa = new SetupApplication(ifac);
		sa.constructCallgraph();
		InfoflowCFG icfg = new InfoflowCFG();
		
		SootMethod dummyMainMethod = sa.getDummyMainMethod();
		
		SymbolicExecutioner se = new SymbolicExecutioner(icfg, dummyMainMethod);
		se.execute();
	}
}