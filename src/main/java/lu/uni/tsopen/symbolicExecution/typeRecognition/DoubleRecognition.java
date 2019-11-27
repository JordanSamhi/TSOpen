package lu.uni.tsopen.symbolicExecution.typeRecognition;

import lu.uni.tsopen.symbolicExecution.SymbolicExecution;
import lu.uni.tsopen.symbolicExecution.methodRecognizers.numeric.GetLatitudeRecognition;
import lu.uni.tsopen.symbolicExecution.methodRecognizers.numeric.GetLongitudeRecognition;
import lu.uni.tsopen.utils.Constants;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;

public class DoubleRecognition extends NumericRecognition {

	public DoubleRecognition(TypeRecognitionHandler next, SymbolicExecution se, InfoflowCFG icfg) {
		super(next, se, icfg);
		this.authorizedTypes.add(Constants.DOUBLE);
		this.nmrh = new GetLongitudeRecognition(null, se);
		this.nmrh = new GetLatitudeRecognition(this.nmrh, se);
	}

}
