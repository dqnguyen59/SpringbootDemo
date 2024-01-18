package org.smartblackbox.springbootdemo.utils;

/**
 * 
 * @author Copyright (C) 2024  Duy Quoc Nguyen <d.q.nguyen@smartblackbox.org>
 *
 */
public class Utils {

	public static String getIndentStr(int indent) {
		return String.format("%1$-" + indent + "s", "");
	}

	public static void printField(String field, int fieldLength, String value, String unit) {
		String fieldStr = String.format("%1$-" + fieldLength + "s: ", field);
		System.out.println(fieldStr + value + " " + unit);
	}
	
	public static void printField(String field, int fieldLength, String value) {
		printField(field, fieldLength, value, "");
	}
	
	public static void printField(String field, int fieldLength, int valueLength, String value1, String value2, String unit) {
		String fieldStr = String.format("%1$-" + fieldLength + "s: ", field);
		String valueStr1 = String.format("%1$-" + valueLength + "s", value1);
		String valueStr2 = String.format("%1$-" + valueLength + "s", value2);
		System.out.println(fieldStr + valueStr1 + valueStr2 + " " + unit);
	}
	
	public static void printField(String field, int fieldLength, int value, String unit) {
		printField(field, fieldLength, "" + value, unit);
	}

	public static void printField(String field, int fieldLength, int value) {
		printField(field, fieldLength, value, "");		
	}

	public static void printField(String field, int fieldLength, int valueLength, int value1, int value2, String unit) {
		printField(field, fieldLength, valueLength, "" + value1, "" + value2, unit);		
	}

	public static void printField(String field, int fieldLength, int valueLength, int value1, int value2) {
		printField(field, fieldLength, valueLength, "" + value1, "" + value2, "");		
	}

	public static long getDiffMS(long nanoTime, long currentTime) {
		return (System.nanoTime() - currentTime) / Consts.MILLISECONDS;		
	}
	
}
