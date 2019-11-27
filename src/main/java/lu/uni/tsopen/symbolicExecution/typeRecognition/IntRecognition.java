package lu.uni.tsopen.symbolicExecution.typeRecognition;

import lu.uni.tsopen.symbolicExecution.SymbolicExecution;
import lu.uni.tsopen.symbolicExecution.methodRecognizers.numeric.GetHoursRecognition;
import lu.uni.tsopen.symbolicExecution.methodRecognizers.numeric.GetMinutesRecognition;
import lu.uni.tsopen.symbolicExecution.methodRecognizers.numeric.GetMonthRecognition;
import lu.uni.tsopen.symbolicExecution.methodRecognizers.numeric.GetRecognition;
import lu.uni.tsopen.symbolicExecution.methodRecognizers.numeric.GetSecondsRecognition;
import lu.uni.tsopen.symbolicExecution.methodRecognizers.numeric.GetYearRecognition;
import lu.uni.tsopen.utils.Constants;
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
