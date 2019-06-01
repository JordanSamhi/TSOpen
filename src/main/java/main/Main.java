package main;

import soot.SootMethod;
import soot.Unit;
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;
import symbolicExecution.SymbolicExecutioner;

public class Main {

	public static void main(String[] args) {
		InfoflowAndroidConfiguration ifac = new InfoflowAndroidConfiguration();
		ifac.getAnalysisFileConfig().setAndroidPlatformDir("/home/jordan/Android/Sdk/platforms/");
		ifac.getAnalysisFileConfig().setTargetAPKFile("/home/jordan/eclipse-workspace/TSOpen/apks/b6d0bb5f7e7a5d1cd151a6a979b9e38d.apk");
		
		SetupApplication sa = new SetupApplication(ifac);
		sa.setCallbackFile("/home/jordan/git/FlowDroid/soot-infoflow-android/AndroidCallbacks.txt");
		sa.constructCallgraph();
		InfoflowCFG icfg = new InfoflowCFG();
		
		SootMethod dummyMainMethod = sa.getDummyMainMethod();
		// Forward analysis so unique starting point
		Unit entryPoint = icfg.getStartPointsOf(dummyMainMethod).iterator().next();
		
		SymbolicExecutioner se = new SymbolicExecutioner(icfg);
		se.execute(entryPoint);
	}
}
