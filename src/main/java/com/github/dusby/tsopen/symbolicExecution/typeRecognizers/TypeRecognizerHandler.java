package com.github.dusby.tsopen.symbolicExecution.typeRecognizers;

import java.util.LinkedList;
import java.util.List;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;

import soot.Unit;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.InvokeStmt;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;

public abstract class TypeRecognizerHandler implements TypeRecognizer {

	protected static final String UNKNOWN_STRING = "UNKNOWN_STRING";
	protected static final String EMPTY_STRING = "";
	protected static final String GET_INSTANCE_METHOD = "getInstance";
	protected static final String NOW_METHOD = "now";

	protected static final String NOW_TAG = "#now";
	protected static final String HERE_TAG = "#here";

	protected static final String JAVA_UTIL_CALENDAR = "java.util.Calendar";
	protected static final String JAVA_UTIL_DATE = "java.util.Date";
	protected static final String JAVA_UTIL_GREGORIAN_CALENDAR = "java.util.Calendar";
	protected static final String JAVA_TIME_LOCAL_DATE_TIME = "java.time.LocalDateTime";
	protected static final String JAVA_TIME_LOCAL_DATE = "java.time.LocalDate";
	protected static final String JAVA_LANG_STRING = "java.lang.String";
	protected static final String JAVA_LANG_STRING_BUILDER = "java.lang.StringBuilder";
	protected static final String JAVA_LANG_STRING_BUFFER = "java.lang.StringBuffer";

	private TypeRecognizerHandler next;
	protected SymbolicExecution se;
	protected InfoflowCFG icfg;
	protected List<String> authorizedTypes;

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	public TypeRecognizerHandler(TypeRecognizerHandler next, SymbolicExecution se, InfoflowCFG icfg) {
		this.next = next;
		this.se = se;
		this.icfg = icfg;
		this.authorizedTypes = new LinkedList<String>();
	}

	@Override
	public List<Pair<Value, SymbolicValue>> recognize(Unit node) {

		List<Pair<Value, SymbolicValue>> result = null;

		if(node instanceof DefinitionStmt) {
			result = this.processRecognitionOfDefStmt((DefinitionStmt) node);
		}else if (node instanceof InvokeStmt) {
			result = this.processRecognitionOfInvokeStmt((InvokeStmt) node);
		}

		if(result != null && !result.isEmpty()) {
			return result;
		}
		if(this.next != null) {
			return this.next.recognize(node);
		}
		else {
			return null;
		}
	}

	protected boolean isAuthorizedType(String type) {
		return this.authorizedTypes.contains(type);
	}
}