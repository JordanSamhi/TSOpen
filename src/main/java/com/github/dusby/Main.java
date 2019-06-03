package com.github.dusby;

import com.github.dusby.symbolicExecution.SymbolicExecutioner;
import soot.SootMethod;
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;

public class Main {

	public static void main(String[] args) {
		InfoflowAndroidConfiguration ifac = new InfoflowAndroidConfiguration();
		ifac.getAnalysisFileConfig().setAndroidPlatformDir("/home/jordan/Android/Sdk/platforms/");
		// small apk
		ifac.getAnalysisFileConfig().setTargetAPKFile("/home/jordan/eclipse-workspace/TSOpen/apks/2337a421045aaebe2be6497dab822826.apk");
//		ifac.getAnalysisFileConfig().setTargetAPKFile("/home/jordan/eclipse-workspace/TSOpen/apks/holycolbert10.apk");
		
		SetupApplication sa = new SetupApplication(ifac);
		sa.setCallbackFile("/home/jordan/git/FlowDroid/soot-infoflow-android/AndroidCallbacks.txt");
		sa.constructCallgraph();
		InfoflowCFG icfg = new InfoflowCFG();
		
		SootMethod dummyMainMethod = sa.getDummyMainMethod();
		
		SymbolicExecutioner se = new SymbolicExecutioner(icfg, dummyMainMethod);
		se.execute();
	}
}