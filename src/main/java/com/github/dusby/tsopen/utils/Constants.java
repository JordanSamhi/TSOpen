package com.github.dusby.tsopen.utils;

public class Constants {
	public static final String UNKNOWN_STRING = "UNKNOWN_STRING";
	public static final String EMPTY_STRING = "";
	public static final String NULL = "null";

	/**
	 * Methods
	 */
	public static final String GET_INSTANCE = "getInstance";
	public static final String NOW = "now";
	public static final String GET_LAST_KNOW_LOCATION = "getLastKnownLocation";
	public static final String GET_LAST_LOCATION = "getLastLocation";
	public static final String CREATE_FROM_PDU = "createFromPdu";
	public static final String GET_LONGITUDE = "getLongitude";
	public static final String GET_LATITUDE = "getLatitude";
	public static final String CURRENT_TIME_MILLIS = "currentTimeMillis";
	public static final String SET_TO_NOW = "setToNow";
	public static final String GET_MINUTES = "getMinutes";
	public static final String GET_SECONDS = "getSeconds";
	public static final String GET_HOURS = "getHours";
	public static final String GET_YEAR = "getYear";
	public static final String GET_MONTH = "getMonth";
	public static final String APPEND = "append";
	public static final String VALUEOF = "valueOf";
	public static final String SUBSTRING = "substring";
	public static final String TOSTRING = "toString";
	public static final String GET_MESSAGE_BODY = "getMessageBody";
	public static final String GET_DISPLAY_MESSAGE_BODY = "getDisplayMessageBody";
	public static final String GET_ORIGINATING_ADDRESS = "getOriginatingAddress";
	public static final String GET_DISPLAY_ORIGINATING_ADDRESS = "getDisplayOriginatingAddress";
	public static final String AFTER = "after";
	public static final String BEFORE = "before";
	public static final String EQUALS = "equals";
	public static final String CONTAINS = "contains";
	public static final String STARTS_WITH = "startsWith";
	public static final String MATCHES = "matches";
	public static final String FORMAT = "format";

	/**
	 * Tags
	 */
	public static final String NOW_TAG = "#now";
	public static final String HERE_TAG = "#here";
	public static final String SMS_TAG = "#sms";
	public static final String LONGITUDE_TAG = "#here/#longitude";
	public static final String LATITUDE_TAG = "#here/#latitude";
	public static final String SECONDS_TAG = "#now/#seconds";
	public static final String MINUTES_TAG = "#now/#minutes";
	public static final String HOUR_TAG = "#now/#hour";
	public static final String YEAR_TAG = "#now/#year";
	public static final String MONTH_TAG = "#now/#month";
	public static final String SMS_BODY_TAG = "#sms/#body";
	public static final String SMS_SENDER_TAG = "#sms/#sender";
	public static final String SUSPICIOUS = "#Suspicious";

	/**
	 * Classes, types
	 */
	public static final String JAVA_UTIL_CALENDAR = "java.util.Calendar";
	public static final String JAVA_TEXT_SIMPLE_DATE_FORMAT = "java.text.SimpleDateFormat";
	public static final String JAVA_UTIL_DATE = "java.util.Date";
	public static final String JAVA_UTIL_GREGORIAN_CALENDAR = "java.util.Calendar";
	public static final String JAVA_TIME_LOCAL_DATE_TIME = "java.time.LocalDateTime";
	public static final String JAVA_TIME_LOCAL_DATE = "java.time.LocalDate";
	public static final String ANDROID_TEXT_FORMAT_TIME = "android.text.format.Time";
	public static final String JAVA_LANG_STRING = "java.lang.String";
	public static final String JAVA_LANG_STRING_BUILDER = "java.lang.StringBuilder";
	public static final String JAVA_LANG_STRING_BUFFER = "java.lang.StringBuffer";
	public static final String ANDROID_LOCATION_LOCATION = "android.location.Location";
	public static final String ANDROID_LOCATION_LOCATION_MANAGER = "android.location.LocationManager";
	public static final String COM_GOOGLE_ANDROID_GMS_LOCATION_LOCATION_RESULT = "com.google.android.gms.location.LocationResult";
	public static final String ANDROID_TELEPHONY_SMSMESSAGE = "android.telephony.SmsMessage";
	public static final String JAVA_LANG_SYSTEM = "java.lang.System";
	public static final String INT = "int";
	public static final String LONG = "long";
	public static final String BOOLEAN = "boolean";

	/**
	 * Files
	 */
	public static final String SENSITIVE_METHODS_FILE = "src/main/resources/sensitiveMethods.pscout";
}
