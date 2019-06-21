package com.github.dusby.tsopen.symbolicExecution.typeRecognition;

import java.util.LinkedList;
import java.util.List;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dusby.tsopen.symbolicExecution.SymbolicExecution;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.ObjectValue;
import com.github.dusby.tsopen.symbolicExecution.symbolicValues.SymbolicValue;

import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;

public abstract class TypeRecognitionHandler implements TypeRecognition {

	protected static final String UNKNOWN_STRING = "UNKNOWN_STRING";
	protected static final String EMPTY_STRING = "";
	protected static final String GET_INSTANCE = "getInstance";
	protected static final String NOW = "now";
	protected static final String GET_LAST_KNOW_LOCATION = "getLastKnownLocation";
	protected static final String GET_LAST_LOCATION = "getLastLocation";
	protected static final String CREATE_FROM_PDU = "createFromPdu";

	protected static final String NOW_TAG = "#now";
	protected static final String HERE_TAG = "#here";
	protected static final String SMS_TAG = "#sms";

	protected static final String JAVA_UTIL_CALENDAR = "java.util.Calendar";
	protected static final String JAVA_UTIL_DATE = "java.util.Date";
	protected static final String JAVA_UTIL_GREGORIAN_CALENDAR = "java.util.Calendar";
	protected static final String JAVA_TIME_LOCAL_DATE_TIME = "java.time.LocalDateTime";
	protected static final String JAVA_TIME_LOCAL_DATE = "java.time.LocalDate";
	protected static final String JAVA_LANG_STRING = "java.lang.String";
	protected static final String JAVA_LANG_STRING_BUILDER = "java.lang.StringBuilder";
	protected static final String JAVA_LANG_STRING_BUFFER = "java.lang.StringBuffer";
	protected static final String ANDROID_LOCATION_LOCATION = "android.location.Location";
	protected static final String ANDROID_LOCATION_LOCATION_MANAGER = "android.location.LocationManager";
	protected static final String COM_GOOGLE_ANDROID_GMS_LOCATION_LOCATION_RESULT = "com.google.android.gms.location.LocationResult";
	protected static final String ANDROID_TELEPHONY_SMSMESSAGE = "android.telephony.SmsMessage";

	private TypeRecognitionHandler next;
	protected SymbolicExecution se;
	protected InfoflowCFG icfg;
	protected List<String> authorizedTypes;

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	public TypeRecognitionHandler(TypeRecognitionHandler next, SymbolicExecution se, InfoflowCFG icfg) {
		this.next = next;
		this.se = se;
		this.icfg = icfg;
		this.authorizedTypes = new LinkedList<String>();
	}

	@Override
	public List<Pair<Value, SymbolicValue>> recognizeType(Unit node) {

		List<Pair<Value, SymbolicValue>> result = null;

		if(node instanceof DefinitionStmt) {
			result = this.processDefinitionStmt((DefinitionStmt) node);
		}else if (node instanceof InvokeStmt) {
			result = this.processInvokeStmt((InvokeStmt) node);
		}

		if(result != null && !result.isEmpty()) {
			return result;
		}
		if(this.next != null) {
			return this.next.recognizeType(node);
		}
		else {
			return null;
		}
	}

	@Override
	public List<Pair<Value, SymbolicValue>> processDefinitionStmt(DefinitionStmt defUnit) {
		if(this.isAuthorizedType(defUnit.getLeftOp().getType().toString())) {
			return this.handleDefinitionStmt(defUnit);
		}
		return null;
	}

	@Override
	public List<Pair<Value, SymbolicValue>> processInvokeStmt(InvokeStmt invUnit) {
		Value base = null;
		InvokeExpr invExprUnit = invUnit.getInvokeExpr();
		SootMethod m = null;
		List<Pair<Value, SymbolicValue>> results = new LinkedList<Pair<Value,SymbolicValue>>();

		if(invExprUnit instanceof SpecialInvokeExpr) {
			m = invExprUnit.getMethod();
			if(m.isConstructor()) {
				base = ((SpecialInvokeExpr) invExprUnit).getBase();
				if(this.isAuthorizedType(base.getType().toString())) {
					this.handleConstructor(invExprUnit, base, results);
				}
			}
		}
		return results;
	}

	@Override
	public void handleConstructor(InvokeExpr invExprUnit, Value base, List<Pair<Value, SymbolicValue>> results) {
		List<Value> args = null;
		ObjectValue object = null;

		args = invExprUnit.getArgs();
		object = new ObjectValue(base.getType(), args, this.se);
		this.handleConstructorTag(args, object);
		results.add(new Pair<Value, SymbolicValue>(base, object));
	}

	protected boolean isAuthorizedType(String type) {
		return this.authorizedTypes.contains(type);
	}
}