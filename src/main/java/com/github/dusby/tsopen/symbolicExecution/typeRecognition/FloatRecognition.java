package com.github.dusby.tsopen.symbolicExecution.typeRecognition;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;
import com.github.dusby.tsopen.utils.Constants;

import soot.jimple.infoflow.solver.cfg.InfoflowCFG;

public class FloatRecognition extends NumericRecognition {

	public FloatRecognition(TypeRecognitionHandler next, SymbolicExecution se, InfoflowCFG icfg) {
		super(next, se, icfg);
		this.authorizedTypes.add(Constants.FLOAT);
	}

}