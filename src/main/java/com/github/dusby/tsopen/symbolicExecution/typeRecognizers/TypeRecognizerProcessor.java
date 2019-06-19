package com.github.dusby.tsopen.symbolicExecution.typeRecognizers;

import java.util.LinkedList;
import java.util.List;

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
	protected List<String> authorizedTypes;

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	public RecognizerProcessor(RecognizerProcessor next, SymbolicExecutioner se, InfoflowCFG icfg) {
		this.next = next;
		this.se = se;
		this.icfg = icfg;
		this.authorizedTypes = new LinkedList<String>();
	}

	@Override
	public List<Pair<Value, SymbolicValueProvider>> recognize(Unit node) {

		List<Pair<Value, SymbolicValueProvider>> result = this.processRecognition(node);

		if(result != null) {
			return result;
		}
		if(this.next != null) {
			return this.next.recognize(node);
		}
		else {
			return null;
		}
	}

	protected abstract boolean isAuthorizedType(String leftOpType);
}