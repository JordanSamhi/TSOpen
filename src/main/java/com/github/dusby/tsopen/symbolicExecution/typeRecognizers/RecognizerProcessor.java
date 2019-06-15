package com.github.dusby.tsopen.symbolicExecution.typeRecognizers;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecutioner;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValueProvider;

import soot.Unit;
import soot.jimple.DefinitionStmt;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;

public abstract class RecognizerProcessor implements RecognizerProvider {
	
	private RecognizerProcessor next;
	protected SymbolicExecutioner se;
	protected InfoflowCFG icfg;

	public RecognizerProcessor(RecognizerProcessor next, SymbolicExecutioner se, InfoflowCFG icfg) {
		this.next = next;
		this.se = se;
		this.icfg = icfg;
	}

	@Override
	public SymbolicValueProvider recognize(DefinitionStmt defUnit, Unit node) {
		
		SymbolicValueProvider result = this.processRecognition(defUnit, node);
		
		if(result != null) {
			return result;
		}
		if(this.next != null) {
			return this.next.processRecognition(defUnit, node);
		}
		else {
			return null;
		}
	}
}