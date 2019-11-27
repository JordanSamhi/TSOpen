package lu.uni.tsopen.symbolicExecution.typeRecognition;

import lu.uni.tsopen.symbolicExecution.SymbolicExecution;
import lu.uni.tsopen.utils.Constants;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;

public class FloatArrayRecognition extends NumericRecognition {

	public FloatArrayRecognition(TypeRecognitionHandler next, SymbolicExecution se, InfoflowCFG icfg) {
		super(next, se, icfg);
		this.authorizedTypes.add(Constants.FLOAT_ARRAY);
	}
}
