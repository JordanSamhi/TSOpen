package lu.uni.tsopen.symbolicExecution.typeRecognition;

import lu.uni.tsopen.symbolicExecution.SymbolicExecution;
import lu.uni.tsopen.symbolicExecution.methodRecognizers.numeric.CurrentTimeMillisRecognition;
import lu.uni.tsopen.symbolicExecution.methodRecognizers.numeric.GetLatitudeRecognition;
import lu.uni.tsopen.symbolicExecution.methodRecognizers.numeric.GetLongitudeRecognition;
import lu.uni.tsopen.utils.Constants;
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
