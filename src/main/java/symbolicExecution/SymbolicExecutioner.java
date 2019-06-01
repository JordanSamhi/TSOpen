package symbolicExecution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import heros.solver.Pair;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.IfStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;
import symbolicExecution.symbolicValues.SymbolicValueProvider;
import symbolicExecution.typeRecognizers.RecognizerProcessor;
import symbolicExecution.typeRecognizers.StringRecognizer;

public class SymbolicExecutioner {
	private final InfoflowCFG icfg;
	private List<Unit> visitedNodes;
	private Map<Value, SymbolicValueProvider> modelContext; 

	public SymbolicExecutioner(InfoflowCFG icfg) {
		this.icfg = icfg;
		this.visitedNodes = new ArrayList<Unit>();
		this.modelContext = new HashMap<Value, SymbolicValueProvider>();
		for(Entry<Value, SymbolicValueProvider> entry : this.modelContext.entrySet()) {
			System.out.println("- "+entry.getKey());
			if(entry.getValue()!=null)
				System.out.println("- "+entry.getValue().getContextValue());
			System.out.println("================");
		}
	}

	public void execute(Unit unit) {
		if(!this.visitedNodes.contains(unit)) {
			this.visitedNodes.add(unit);
			List<Unit> successors = this.icfg.getSuccsOf(unit);

			if(unit instanceof InvokeStmt) {
				this.propagateTargetMethod(unit);
			}

			else if(unit instanceof DefinitionStmt) {
				DefinitionStmt defUnit = (DefinitionStmt) unit;
				// Chain of relevant object recognizers
				RecognizerProcessor rp = new StringRecognizer(null, this);
				Map<Value, SymbolicValueProvider> defModelResult = rp.recognize(defUnit);
				if(defModelResult != null) {
					this.modelContext.putAll(defModelResult);
				}
				
				if(defUnit.getRightOp() instanceof InvokeExpr) {
					this.propagateTargetMethod(defUnit);
				}
			}

			// Process all successors
			for(Unit successor : successors) {
				this.execute(successor);
			}
		}
	}

	private void propagateTargetMethod(Unit invokation) {
		Collection<SootMethod> pointsToRightOp = this.icfg.getCalleesOfCallAt(invokation);
		for(SootMethod callee : pointsToRightOp) {
			if(!callee.isJavaLibraryMethod()) {
				this.execute(this.icfg.getStartPointsOf(callee).iterator().next());
			}
		}
	}

	public Map<Value, SymbolicValueProvider> getModelContext() {
		return this.modelContext;
	}

	public InfoflowCFG getIcfg() {
		return this.icfg;
	}
}
