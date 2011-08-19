package com.nextmethod;

/**
 *
 */
public final class SystemHelpers {

	private SystemHelpers() {
	}

	private static final String NL = System.getProperty("line.separator");

	public static String NewLine() {
		return NL;
	}


	private static final String FS = System.getProperty("file.separator");

	public static String FileSeparator() {
		return FS;
	}

	private static final String PS = System.getProperty("path.separator");

	public static String PathSeparator() {
		return PS;
	}

	public static String ClassPath() {
		return System.getProperty("java.class.path");
	}
}
