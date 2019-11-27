package lu.uni.tsopen.symbolicExecution.typeRecognition;

import lu.uni.tsopen.symbolicExecution.SymbolicExecution;
import lu.uni.tsopen.utils.Constants;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;

public class FloatRecognition extends NumericRecognition {

	public FloatRecognition(TypeRecognitionHandler next, SymbolicExecution se, InfoflowCFG icfg) {
		super(next, se, icfg);
		this.authorizedTypes.add(Constants.FLOAT);
	}

}
