package lu.uni.tsopen;

/*-
 * #%L
 * TSOpen - Open-source implementation of TriggerScope
 * 
 * Paper describing the approach : https://seclab.ccs.neu.edu/static/publications/sp2016triggerscope.pdf
 * 
 * %%
 * Copyright (C) 2019 Jordan Samhi
 * University of Luxembourg - Interdisciplinary Centre for
 * Security Reliability and Trust (SnT) - All rights reserved
 *
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.profiler.StopWatch;

import lu.uni.tsopen.logicBombs.PotentialLogicBombsRecovery;
import lu.uni.tsopen.pathPredicateRecovery.PathPredicateRecovery;
import lu.uni.tsopen.pathPredicateRecovery.SimpleBlockPredicateExtraction;
import lu.uni.tsopen.symbolicExecution.SymbolicExecution;
import lu.uni.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import lu.uni.tsopen.utils.CommandLineOptions;
import lu.uni.tsopen.utils.Constants;
import lu.uni.tsopen.utils.TimeOut;
import lu.uni.tsopen.utils.Utils;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.IfStmt;
import soot.jimple.infoflow.InfoflowConfiguration.CallgraphAlgorithm;
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.android.manifest.ProcessManifest;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;

public class Analysis {

	private String pkgName;
	private CommandLineOptions options;
	private String fileName;
	private PotentialLogicBombsRecovery plbr;
	private InfoflowCFG icfg;
	private int dexSize;
	private int nbClasses;
	private String fileSha256;
	private SimpleBlockPredicateExtraction sbpe;
	private PathPredicateRecovery ppr;

	private Logger logger = LoggerFactory.getLogger(Main.class);

	StopWatch stopWatchCG = new StopWatch("cg"),
			stopWatchSBPE = new StopWatch("sbpe"),
			stopWatchPPR = new StopWatch("ppr"),
			stopWatchSE = new StopWatch("se"),
			stopWatchPLBR = new StopWatch("plbr"),
			stopWatchAPP = new StopWatch("app");

	public Analysis(String[] args) {
		this.options = new CommandLineOptions(args);
		this.fileName = this.options.getFile();
		this.pkgName = this.getPackageName(this.fileName);
		this.fileSha256  = this.getFileSha256(this.fileName);
		this.sbpe = null;
		this.plbr = null;
		this.icfg = null;
		this.ppr = null;
	}

	public void run() {
		try {
			this.launchAnalysis();
		} catch(Exception e) {
			this.logger.error("Something went wrong : {}", e.getMessage());
			this.logger.error("Ending program...");
			this.printResultsInFile(true);
			if (this.options.hasRaw()) {
				this.printRawResults(true);
			}
			System.exit(0);
		}
	}

	private void launchAnalysis() {
		this.stopWatchAPP.start("app");
		if(!this.options.hasQuiet() && !this.options.hasRaw()) {
			System.out.println(String.format("TSOpen v1.0 started on %s\n", new Date()));
		}

		InfoflowAndroidConfiguration ifac = new InfoflowAndroidConfiguration();
		ifac.setIgnoreFlowsInSystemPackages(false);
		SetupApplication sa = null;
		SootMethod dummyMainMethod = null;
		SymbolicExecution se = null;
		Thread sbpeThread = null,
				pprThread = null,
				seThread = null,
				plbrThread = null;
		TimeOut timeOut = new TimeOut(this.options.getTimeout(), this);
		int timeout = timeOut.getTimeout();
		timeOut.trigger();

		if(!this.options.hasQuiet() && !this.options.hasRaw()) {
			this.logger.info(String.format("%-35s : %s", "Package", this.getPackageName(this.fileName)));
			this.logger.info(String.format("%-35s : %3s %s", "Timeout", timeout, timeout > 1 ? "mins" : "min"));
		}

		this.stopWatchCG.start("CallGraph");
		ifac.getAnalysisFileConfig().setAndroidPlatformDir(this.options.getPlatforms());
		ifac.getAnalysisFileConfig().setTargetAPKFile(this.fileName);

		String cg = this.options.getCallGraph();
		if(cg != null) {
			switch(cg) {
			case "SPARK": ifac.setCallgraphAlgorithm(CallgraphAlgorithm.SPARK);
			break;
			case "CHA": ifac.setCallgraphAlgorithm(CallgraphAlgorithm.CHA);
			break;
			case "VTA": ifac.setCallgraphAlgorithm(CallgraphAlgorithm.VTA);
			break;
			case "RTA": ifac.setCallgraphAlgorithm(CallgraphAlgorithm.RTA);
			break;
			}
		}

		sa = new SetupApplication(ifac);
		sa.getConfig().setMergeDexFiles(true);
		sa.constructCallgraph();
		this.icfg = new InfoflowCFG();
		this.stopWatchCG.stop();

		this.nbClasses = Scene.v().getApplicationClasses().size();

		if(!this.options.hasQuiet() && !this.options.hasRaw()) {
			this.logger.info(String.format("%-35s : %s", "CallGraph construction", Utils.getFormattedTime(this.stopWatchCG.elapsedTime())));
		}

		dummyMainMethod = sa.getDummyMainMethod();
		this.sbpe = new SimpleBlockPredicateExtraction(this.icfg, dummyMainMethod);
		this.ppr = new PathPredicateRecovery(this.icfg, this.sbpe, dummyMainMethod, this.options.hasExceptions());
		se = new SymbolicExecution(this.icfg, dummyMainMethod);
		this.plbr = new PotentialLogicBombsRecovery(this.sbpe, se, this.ppr, this.icfg);

		sbpeThread = new Thread(this.sbpe, "sbpe");
		pprThread = new Thread(this.ppr, "pprr");
		seThread = new Thread(se, "syex");
		plbrThread = new Thread(this.plbr, "plbr");

		this.stopWatchSBPE.start("sbpe");
		sbpeThread.start();
		this.stopWatchSE.start("se");
		seThread.start();

		try {
			sbpeThread.join();
			this.stopWatchSBPE.stop();
			if(!this.options.hasQuiet() && !this.options.hasRaw()) {
				this.logger.info(String.format("%-35s : %s", "Simple Block Predicate Extraction", Utils.getFormattedTime(this.stopWatchSBPE.elapsedTime())));
			}
		} catch (InterruptedException e) {
			this.logger.error(e.getMessage());
		}

		this.stopWatchPPR.start("ppr");
		pprThread.start();

		try {
			pprThread.join();
			this.stopWatchPPR.stop();
			if(!this.options.hasQuiet() && !this.options.hasRaw()) {
				this.logger.info(String.format("%-35s : %s", "Path Predicate Recovery", Utils.getFormattedTime(this.stopWatchPPR.elapsedTime())));
			}
			seThread.join();
			this.stopWatchSE.stop();
			if(!this.options.hasQuiet() && !this.options.hasRaw()) {
				this.logger.info(String.format("%-35s : %s", "Symbolic Execution", Utils.getFormattedTime(this.stopWatchSE.elapsedTime())));
			}
		} catch (InterruptedException e) {
			this.logger.error(e.getMessage());
		}

		this.stopWatchPLBR.start("plbr");
		plbrThread.start();

		try {
			plbrThread.join();
			this.stopWatchPLBR.stop();
			if(!this.options.hasQuiet() && !this.options.hasRaw()) {
				this.logger.info(String.format("%-35s : %s", "Potential Logic Bombs Recovery", Utils.getFormattedTime(this.stopWatchPLBR.elapsedTime())));
			}
		} catch (InterruptedException e) {
			this.logger.error(e.getMessage());
		}

		this.stopWatchAPP.stop();

		if(!this.options.hasQuiet() && !this.options.hasRaw()) {
			this.logger.info(String.format("%-35s : %s", "Application Execution Time", Utils.getFormattedTime(this.stopWatchAPP.elapsedTime())));
		}

		if(this.options.hasOutput()) {
			this.printResultsInFile(false);
		}
		if (!this.options.hasQuiet()){
			if (this.options.hasRaw()) {
				this.printRawResults(false);
			}else if(this.options.hasVerbose()) {
				this.printVerboseResults();
			}else {
				this.printResults();
			}
		}
		timeOut.cancel();
	}

	private void printVerboseResults() {
		SootMethod ifMethod = null;
		SootClass ifClass = null;
		String ifComponent = null;
		IfStmt ifStmt = null;
		int LbCount = 0;
		if(this.plbr.hasPotentialLogicBombs()) {
			System.out.println(String.format("\n APK %s potentially contains the following logic bombs:", this.pkgName));
			System.out.println("----------------------------------------------------------------");
			for(Entry<IfStmt, Pair<List<SymbolicValue>, SootMethod>> e : this.plbr.getPotentialLogicBombs().entrySet()) {
				LbCount += 1;
				System.out.println(String.format("Potential logic bomb %d:", LbCount));
				ifStmt = e.getKey();
				ifMethod = this.icfg.getMethodOf(ifStmt);
				ifClass = ifMethod.getDeclaringClass();
				ifComponent = Utils.getComponentType(ifClass);
				System.out.println(String.format("- It is located in class %s and in method %s", ifClass, ifMethod.getName()));
				System.out.println(String.format("- The following \"if\" statement triggers the "
						+ "potential logic bomb: \"if %s\" which contains the following possible predicates:", ifStmt.getCondition()));
				for(SymbolicValue sv : e.getValue().getValue0()) {
					System.out.println(String.format("    - %s %s", sv.getValue(), sv));
				}
				System.out.println(String.format("- The sequence of method calls to reach this potential logic"
						+ " bomb starts in component of type %s and ends in component of type %s", Utils.getStartingComponent(ifMethod),
						ifComponent));
				System.out.println(String.format("- The sequence of method call to reach this potential logic bomb"
						+ " (call stack) has a length of %s", Utils.join(", ", Utils.getLengthLogicBombCallStack(ifMethod))));
				System.out.println(String.format("- The size of the logical formula to reach the potential logic bomb is %d", this.ppr.getSizeOfFullPath(ifStmt)));
				System.out.println(String.format("- The following sensitive method was used to qualify the trigger as potentially a logic bomb: %s", e.getValue().getValue1().getSignature()));
				System.out.println(Utils.isInCallGraph(ifMethod) ? "- The logic bomb is reachable from the program entry point" : "- The logic bomb is not "
						+ "reachable from the program entry point");
				System.out.println(String.format("- The potential logic bomb contains %d Jimple instructions", Utils.getGuardedBlocksDensity(this.ppr, ifStmt)));
				System.out.println(Utils.isNested(ifStmt, this.icfg, this.plbr, this.ppr) ? "- The potential logic bomb is nested in another" : "- The potential logic bomb is not nested in another");
				System.out.println("----------------------------------------------------------------\n");
			}
		}else {
			System.out.println("\nNo Logic Bomb found\n");
		}
	}

	private void printResults() {
		SootMethod ifMethod = null;
		SootClass ifClass = null;
		String ifComponent = null;
		IfStmt ifStmt = null;
		if(this.plbr.hasPotentialLogicBombs()) {
			System.out.println("\nPotential Logic Bombs found : ");
			System.out.println("----------------------------------------------------------------");
			for(Entry<IfStmt, Pair<List<SymbolicValue>, SootMethod>> e : this.plbr.getPotentialLogicBombs().entrySet()) {
				ifStmt = e.getKey();
				ifMethod = this.icfg.getMethodOf(ifStmt);
				ifClass = ifMethod.getDeclaringClass();
				ifComponent = Utils.getComponentType(ifClass);
				System.out.println(String.format("- %-25s : if %s", "Statement", ifStmt.getCondition()));
				System.out.println(String.format("- %-25s : %s", "Class", ifClass));
				System.out.println(String.format("- %-25s : %s", "Method", ifMethod.getName()));
				System.out.println(String.format("- %-25s : %s", "Starting Component", Utils.getStartingComponent(ifMethod)));
				System.out.println(String.format("- %-25s : %s", "Ending Component", ifComponent));
				System.out.println(String.format("- %-25s : %s", "CallStack length", Utils.join(", ", Utils.getLengthLogicBombCallStack(ifMethod))));
				System.out.println(String.format("- %-25s : %s", "Size of formula", this.ppr.getSizeOfFullPath(ifStmt)));
				System.out.println(String.format("- %-25s : %s", "Sensitive method", e.getValue().getValue1().getSignature()));
				System.out.println(String.format("- %-25s : %s", "Reachable", Utils.isInCallGraph(ifMethod) ? "Yes" : "No"));
				System.out.println(String.format("- %-25s : %s", "Guarded Blocks Density", Utils.getGuardedBlocksDensity(this.ppr, ifStmt)));
				System.out.println(String.format("- %-25s : %s", "Nested", Utils.isNested(ifStmt, this.icfg, this.plbr, this.ppr) ? "Yes" : "No"));
				for(SymbolicValue sv : e.getValue().getValue0()) {
					System.out.println(String.format("- %-25s : %s (%s)", "Predicate", sv.getValue(), sv));
				}
				System.out.println("----------------------------------------------------------------\n");
			}
		}else {
			System.out.println("\nNo Logic Bomb found\n");
		}
	}

	/**
	 * Print analysis results in the given file
	 */
	private void printResultsInFile(boolean timeoutReached) {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new FileOutputStream(new File(this.options.getOutput()), true));
			writer.append(this.getRawResults(timeoutReached));
			writer.close();
		} catch (Exception e) {
			this.logger.error(e.getMessage());
		}
	}

	private void printRawResults(boolean timeoutReached) {
		System.out.println(this.getRawResults(timeoutReached));
	}

	/**
	 * Format :
	 * [sha256], [pkg_name], [count_of_triggers], [elapsed_time], [hasSuspiciousTrigger],
	 * [hasSuspiciousTriggerAfterControlDependency], [hasSuspiciousTriggerAfterPostFilters],
	 * [dex_size], [count_of_classes], [count_of_if], [if_depth], [count_of_objects]
	 * @param timeoutReached
	 * @return
	 */
	private String getRawResults(boolean timeoutReached) {
		SootMethod ifMethod = null;
		SootClass ifClass = null;
		String ifStmtStr = null,
				ifComponent = null;
		List<SymbolicValue> values = null,
				visitedValues = new ArrayList<SymbolicValue>();
		SymbolicValue sv = null;
		IfStmt ifStmt = null;
		String symbolicValues = null;
		StringBuilder result = new StringBuilder();
		result.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%d,%d,%d,%d,%d,%d\n", this.fileSha256, this.pkgName,
				timeoutReached ? 0 : this.plbr.getPotentialLogicBombs().size(), timeoutReached ? -1 : TimeUnit.SECONDS.convert(this.stopWatchAPP.elapsedTime(), TimeUnit.NANOSECONDS),
						this.plbr == null ? 0 : this.plbr.ContainsSuspiciousCheck() ? 1 : 0, this.plbr == null ? 0 : this.plbr.ContainsSuspiciousCheckAfterControlDependency() ? 1 : 0,
								this.plbr == null ? 0 : this.plbr.ContainsSuspiciousCheckAfterPostFilterStep() ? 1 : 0, this.dexSize, this.nbClasses,
										this.sbpe == null ? 0 : this.sbpe.getIfCount(), this.sbpe == null ? 0 : this.sbpe.getIfDepthInMethods(), this.sbpe == null ? 0 : this.sbpe.getCountOfObject(),
												TimeUnit.MILLISECONDS.convert(this.stopWatchCG.elapsedTime(), TimeUnit.NANOSECONDS),
												TimeUnit.MILLISECONDS.convert(this.stopWatchPLBR.elapsedTime(), TimeUnit.NANOSECONDS),
												TimeUnit.MILLISECONDS.convert(this.stopWatchPPR.elapsedTime(), TimeUnit.NANOSECONDS),
												TimeUnit.MILLISECONDS.convert(this.stopWatchSBPE.elapsedTime(), TimeUnit.NANOSECONDS),
												TimeUnit.MILLISECONDS.convert(this.stopWatchSE.elapsedTime(), TimeUnit.NANOSECONDS),
												timeoutReached ? -1 : TimeUnit.MILLISECONDS.convert(this.stopWatchAPP.elapsedTime(), TimeUnit.NANOSECONDS)));
		if(this.plbr != null) {
			for(Entry<IfStmt, Pair<List<SymbolicValue>, SootMethod>> e : this.plbr.getPotentialLogicBombs().entrySet()) {
				ifStmt = e.getKey();
				symbolicValues = "";
				ifMethod = this.icfg.getMethodOf(ifStmt);
				ifClass = ifMethod.getDeclaringClass();
				ifStmtStr = String.format("if %s", ifStmt.getCondition());
				ifComponent = Utils.getComponentType(ifMethod.getDeclaringClass());
				symbolicValues += String.format("%s%s;%s;%s;%s;%s;%s;%s;%s;%s;%s;%s;%s;", Constants.FILE_LOGIC_BOMBS_DELIMITER,
						ifStmtStr, ifClass, ifMethod.getName(), e.getValue().getValue1().getSignature(), ifComponent,
						this.ppr.getSizeOfFullPath(ifStmt), Utils.isInCallGraph(ifMethod) ? 1 : 0, Utils.getStartingComponent(ifMethod),
								Utils.getGuardedBlocksDensity(this.ppr, ifStmt), Utils.join(", ", Utils.getLengthLogicBombCallStack(ifMethod)),
								Utils.guardedBlocksContainApplicationInvoke(this.ppr, ifStmt) ? 1 : 0,
										Utils.isNested(ifStmt, this.icfg, this.plbr, this.ppr) ? 1 : 0);
				values = e.getValue().getValue0();
				visitedValues.clear();
				for(int i = 0 ; i < values.size() ; i++) {
					sv = values.get(i);
					if(!visitedValues.contains(sv)) {
						visitedValues.add(sv);
						if(i != 0) {
							symbolicValues += ":";
						}
						symbolicValues += String.format("%s (%s)", sv.getValue(), sv);
					}
				}
				symbolicValues += "\n";
				result.append(symbolicValues);
			}
		}
		return result.toString();
	}

	public void timeoutReachedPrintResults() {
		this.printResultsInFile(true);
		if(this.options.hasRaw()) {
			this.printRawResults(true);
		}
	}

	private String getPackageName(String fileName) {
		String pkgName = null;
		ProcessManifest pm = null;
		try {
			pm = new ProcessManifest(fileName);
			pkgName = pm.getPackageName();
			this.dexSize = pm.getApk().getInputStream(Constants.CLASSES_DEX).available();
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
