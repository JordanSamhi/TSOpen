package com.github.dusby.tsopen.symbolicExecution.typeRecognizers;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecutioner;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValueProvider;

import soot.Unit;
import soot.Value;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;

public abstract class RecognizerProcessor implements RecognizerProvider {
	
	private RecognizerProcessor next;
	protected SymbolicExecutioner se;
	protected InfoflowCFG icfg;
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	public RecognizerProcessor(RecognizerProcessor next, SymbolicExecutioner se, InfoflowCFG icfg) {
		this.next = next;
		this.se = se;
		this.icfg = icfg;
	}

	@Override
	public Pair<Value, SymbolicValueProvider> recognize(Unit node) {
		
		Pair<Value, SymbolicValueProvider> result = this.processRecognition(node);
		
		if(result != null) {
			return result;
		}
		if(this.next != null) {
			return this.next.processRecognition(node);
		}
		else {
			return null;
		}
	}
}