package com.github.dusby.tsopen.symbolicExecution.typeRecognition;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;
import com.github.dusby.tsopen.symbolicExecution.methodRecognizers.numeric.GetLatitudeRecognition;
import com.github.dusby.tsopen.symbolicExecution.methodRecognizers.numeric.GetLongitudeRecognition;
import com.github.dusby.tsopen.utils.Constants;

import soot.jimple.infoflow.solver.cfg.InfoflowCFG;

public class DoubleRecognition extends NumericRecognition {

	public DoubleRecognition(TypeRecognitionHandler next, SymbolicExecution se, InfoflowCFG icfg) {
		super(next, se, icfg);
		this.authorizedTypes.add(Constants.DOUBLE);
		this.nmrh = new GetLongitudeRecognition(null, se);
		this.nmrh = new GetLatitudeRecognition(this.nmrh, se);
	}

}
