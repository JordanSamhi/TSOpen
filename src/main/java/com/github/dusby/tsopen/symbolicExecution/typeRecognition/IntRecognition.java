package com.github.dusby.tsopen.symbolicExecution.typeRecognition;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;
import com.github.dusby.tsopen.symbolicExecution.methodRecognizers.numeric.GetMinutesRecognition;
import com.github.dusby.tsopen.symbolicExecution.methodRecognizers.numeric.GetMonthRecognition;
import com.github.dusby.tsopen.symbolicExecution.methodRecognizers.numeric.GetSecondsRecognition;
import com.github.dusby.tsopen.utils.Constants;

import soot.jimple.infoflow.solver.cfg.InfoflowCFG;

public class IntRecognition extends NumericRecognition {

	public IntRecognition(TypeRecognitionHandler next, SymbolicExecution se, InfoflowCFG icfg) {
		super(next, se, icfg);
		this.authorizedTypes.add(Constants.INT);
		this.lmrh = new GetMonthRecognition(null, se);
		this.lmrh = new GetMinutesRecognition(this.lmrh, se);
		this.lmrh = new GetSecondsRecognition(this.lmrh, se);
	}
}
