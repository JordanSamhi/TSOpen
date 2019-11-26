package com.github.JordanSamhi.tsopen.symbolicExecution.typeRecognition;

import com.github.JordanSamhi.tsopen.symbolicExecution.SymbolicExecution;
import com.github.JordanSamhi.tsopen.symbolicExecution.methodRecognizers.numeric.CurrentTimeMillisRecognition;
import com.github.JordanSamhi.tsopen.symbolicExecution.methodRecognizers.numeric.GetLatitudeRecognition;
import com.github.JordanSamhi.tsopen.symbolicExecution.methodRecognizers.numeric.GetLongitudeRecognition;
import com.github.JordanSamhi.tsopen.utils.Constants;

import soot.jimple.infoflow.solver.cfg.InfoflowCFG;

public class LongRecognition extends NumericRecognition {

	public LongRecognition(TypeRecognitionHandler next, SymbolicExecution se, InfoflowCFG icfg) {
		super(next, se, icfg);
		this.authorizedTypes.add(Constants.LONG);
		this.nmrh = new GetLongitudeRecognition(null, se);
		this.nmrh = new GetLatitudeRecognition(this.nmrh, se);
		this.nmrh = new CurrentTimeMillisRecognition(this.nmrh, se);
	}
}
