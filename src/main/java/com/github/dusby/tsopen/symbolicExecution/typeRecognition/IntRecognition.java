package com.github.dusby.tsopen.symbolicExecution.typeRecognition;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;
import com.github.dusby.tsopen.symbolicExecution.methodRecognizers.numeric.GetHoursRecognition;
import com.github.dusby.tsopen.symbolicExecution.methodRecognizers.numeric.GetMinutesRecognition;
import com.github.dusby.tsopen.symbolicExecution.methodRecognizers.numeric.GetMonthRecognition;
import com.github.dusby.tsopen.symbolicExecution.methodRecognizers.numeric.GetRecognition;
import com.github.dusby.tsopen.symbolicExecution.methodRecognizers.numeric.GetSecondsRecognition;
import com.github.dusby.tsopen.symbolicExecution.methodRecognizers.numeric.GetYearRecognition;
import com.github.dusby.tsopen.utils.Constants;

import soot.jimple.infoflow.solver.cfg.InfoflowCFG;

public class IntRecognition extends NumericRecognition {

	public IntRecognition(TypeRecognitionHandler next, SymbolicExecution se, InfoflowCFG icfg) {
		super(next, se, icfg);
		this.authorizedTypes.add(Constants.INT);
		this.nmrh = new GetMonthRecognition(null, se);
		this.nmrh = new GetMinutesRecognition(this.nmrh, se);
		this.nmrh = new GetSecondsRecognition(this.nmrh, se);
		this.nmrh = new GetHoursRecognition(this.nmrh, se);
		this.nmrh = new GetYearRecognition(this.nmrh, se);
		this.nmrh = new GetRecognition(this.nmrh, se);
	}
}
