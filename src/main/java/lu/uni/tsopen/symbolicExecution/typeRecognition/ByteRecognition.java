package lu.uni.tsopen.symbolicExecution.typeRecognition;

import java.util.LinkedList;
import java.util.List;

import org.javatuples.Pair;

import lu.uni.tsopen.symbolicExecution.SymbolicExecution;
import lu.uni.tsopen.symbolicExecution.symbolicValues.BinOpValue;
import lu.uni.tsopen.symbolicExecution.symbolicValues.ObjectValue;
import lu.uni.tsopen.symbolicExecution.symbolicValues.SymbolicValue;
import lu.uni.tsopen.utils.Constants;
import lu.uni.tsopen.utils.Utils;
import soot.SootMethod;
import soot.Value;
import soot.jimple.BinopExpr;
import soot.jimple.DefinitionStmt;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;

public class ByteRecognition extends TypeRecognitionHandler {

	public ByteRecognition(TypeRecognitionHandler next, SymbolicExecution se, InfoflowCFG icfg) {
		super(next, se, icfg);
		this.authorizedTypes.add(Constants.BYTE);
	}

	@Override
	public void handleConstructorTag(List<Value> args, ObjectValue object) {}

	@Override
	public List<Pair<Value, SymbolicValue>> handleDefinitionStmt(DefinitionStmt defUnit) {
		Value leftOp = defUnit.getLeftOp(),
				rightOp = defUnit.getRightOp(),
				binOp1 = null,
				binOp2 = null;
		List<Pair<Value, SymbolicValue>> results = new LinkedList<Pair<Value,SymbolicValue>>();
		SymbolicValue object = null;
		BinopExpr BinOpRightOp = null;
		if(rightOp instanceof BinopExpr){
			BinOpRightOp = (BinopExpr) rightOp;
			binOp1 = BinOpRightOp.getOp1();
			binOp2 = BinOpRightOp.getOp2();
			object = new BinOpValue(this.se, binOp1, binOp2, BinOpRightOp.getSymbol());
			Utils.propagateTags(binOp1, object, this.se);
			Utils.propagateTags(binOp2, object, this.se);
		}else {
			return results;
		}
		results.add(new Pair<Value, SymbolicValue>(leftOp, object));
		return results;
	}

	@Override
	public void handleInvokeTag(List<Value> args, Value base, SymbolicValue object, SootMethod method) {}

}
