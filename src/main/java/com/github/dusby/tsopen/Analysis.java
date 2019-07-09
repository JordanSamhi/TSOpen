package com.github.dusby.tsopen;

import java.io.FileInputStream;
import java.io.PrintWriter;
import java.security.MessageDigest;
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
import com.github.dusby.tsopen.utils.CommandLineOptions;
import com.github.dusby.tsopen.utils.TimeOut;
import com.github.dusby.tsopen.utils.Utils;

import soot.SootMethod;
import soot.jimple.IfStmt;
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.android.manifest.ProcessManifest;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;

public class Analysis {

	private String pkgName;
	private CommandLineOptions options;
	String fileName;

	private Logger logger = LoggerFactory.getLogger(Main.class);
	private Profiler mainProfiler = new Profiler(Main.class.getName());

	public Analysis(String[] args) {
		this.options = new CommandLineOptions(args);
		this.fileName = this.options.getFile();
		this.pkgName = this.getPackageName(this.fileName);
	}

	public void run() {
		StopWatch stopWatchCG = new StopWatch("cg"),
				stopWatchSBPE = new StopWatch("sbpe"),
				stopWatchPPR = new StopWatch("ppr"),
				stopWatchSE = new StopWatch("se"),
				stopWatchPLBR = new StopWatch("plbr");
		this.mainProfiler.start("overall");
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
		TimeOut timeOut = new TimeOut(this.options.getTimeout());
		timeOut.trigger();

		this.logger.info(String.format("%-35s : %s", "Package", this.getPackageName(this.fileName)));
		this.logger.info(String.format("%-35s : %3s %s", "Timeout", timeOut.getTimeout(), "mins"));

		stopWatchCG.start("CallGraph");
		ifac.getAnalysisFileConfig().setAndroidPlatformDir(this.options.getPlatforms());
		ifac.getAnalysisFileConfig().setTargetAPKFile(this.fileName);

		sa = new SetupApplication(ifac);
		sa.constructCallgraph();
		icfg = new InfoflowCFG();
		stopWatchCG.stop();
		this.logger.info(String.format("%-35s : %s", "CallGraph construction", Utils.getFormattedTime(stopWatchCG.elapsedTime())));

		dummyMainMethod = sa.getDummyMainMethod();
		sbpe = new SimpleBlockPredicateExtraction(icfg, dummyMainMethod);
		ppr = new PathPredicateRecovery(icfg, sbpe, dummyMainMethod, this.options.hasExceptions());
		se = new SymbolicExecution(icfg, dummyMainMethod);
		plbr = new PotentialLogicBombsRecovery(sbpe, se, ppr, icfg);

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
			this.logger.info(String.format("%-35s : %s", "Simple Block Predicate Extraction", Utils.getFormattedTime(stopWatchSBPE.elapsedTime())));
		} catch (InterruptedException e) {
			this.logger.error(e.getMessage());
		}

		stopWatchPPR.start("ppr");
		pprThread.start();

		try {
			pprThread.join();
			stopWatchPPR.stop();
			this.logger.info(String.format("%-35s : %s", "Path Predicate Recovery", Utils.getFormattedTime(stopWatchPPR.elapsedTime())));
			seThread.join();
			stopWatchSE.stop();
			this.logger.info(String.format("%-35s : %s", "Symbolic Execution", Utils.getFormattedTime(stopWatchSE.elapsedTime())));
		} catch (InterruptedException e) {
			this.logger.error(e.getMessage());
		}

		stopWatchPLBR.start("plbr");
		plbrThread.start();

		try {
			plbrThread.join();
			stopWatchPLBR.stop();
			this.logger.info(String.format("%-35s : %s", "Potential Logic Bombs Recovery", Utils.getFormattedTime(stopWatchPLBR.elapsedTime())));
		} catch (InterruptedException e) {
			this.logger.error(e.getMessage());
		}

		this.mainProfiler.stop();
		this.logger.info(String.format("%-35s : %s", "Application Execution Time", Utils.getFormattedTime(this.mainProfiler.elapsedTime())));

		this.printResults(plbr, icfg);
		timeOut.cancel();
	}

	private void printResults(PotentialLogicBombsRecovery plbr, InfoflowCFG icfg) {
		//TODO print object symbolic values
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
				System.out.println("----------------------------------------------------------------\n");
			}
		}else {
			System.out.println("\nNo Logic Bomb found\n");
		}
	}

	private void printResultsInFile(PotentialLogicBombsRecovery plbr, InfoflowCFG icfg, String outputFile) {
		PrintWriter writer = null;
		String result = String.format("{},{},{},{}", this.pkgName, this.getFileSha256(this.fileName), plbr.getPotentialLogicBombs().size(), this.mainProfiler.elapsedTime());
		try {
			writer = new PrintWriter(outputFile, "UTF-8");
			writer.write(result);
			writer.close();
		} catch (Exception e) {
			this.logger.error(e.getMessage());
		}
	}

	private String getPackageName(String fileName) {
		String pkgName = null;
		ProcessManifest pm = null;
		try {
			pm = new ProcessManifest(fileName);
			pkgName = pm.getPackageName();
			pm.close();
		} catch (Exception e) {
			this.logger.error(e.getMessage());
		}
		return pkgName;
	}

	private String getFileSha256(String file) {
		MessageDigest sha256 = null;
		FileInputStream fis = null;
		StringBuffer sb = null;
		byte[] data = null;
		int read = 0;
		byte[] hashBytes = null;
		try {

			sha256 = MessageDigest.getInstance("SHA-256");
			fis = new FileInputStream(file);

			data = new byte[1024];
			read = 0;
			while ((read = fis.read(data)) != -1) {
				sha256.update(data, 0, read);
			};
			hashBytes = sha256.digest();

			sb = new StringBuffer();
			for (int i = 0; i < hashBytes.length; i++) {
				sb.append(Integer.toString((hashBytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			fis.close();
		} catch (Exception e) {
			this.logger.error(e.getMessage());
		}
		return sb.toString();
	}
}
