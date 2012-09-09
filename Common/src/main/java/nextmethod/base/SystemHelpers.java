package nextmethod.base;

public final class SystemHelpers {

	private SystemHelpers() {}

	private static final String NL = System.lineSeparator();

	public static String newLine() {
		return NL;
	}

	private static final String FS = System.getProperty("file.separator");

	public static String fileSeparator() {
		return FS;
	}

	private static final String PS = System.getProperty("path.separator");

	public static String pathSeparator() {
		return PS;
	}

}
