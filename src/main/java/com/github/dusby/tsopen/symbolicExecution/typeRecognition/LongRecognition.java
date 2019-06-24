package com.github.dusby.tsopen.symbolicExecution.typeRecognition;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;
import com.github.dusby.tsopen.symbolicExecution.methodRecognizers.numeric.GetLatitudeRecognition;
import com.github.dusby.tsopen.symbolicExecution.methodRecognizers.numeric.GetLongitudeRecognition;
import com.github.dusby.tsopen.utils.Constants;

import soot.jimple.infoflow.solver.cfg.InfoflowCFG;

public class LongRecognition extends NumericRecognition {

	public LongRecognition(TypeRecognitionHandler next, SymbolicExecution se, InfoflowCFG icfg) {
		super(next, se, icfg);
		this.authorizedTypes.add(Constants.LONG);
		this.lmrh = new GetLongitudeRecognition(null, se);
		this.lmrh = new GetLatitudeRecognition(this.lmrh, se);
	}
}
