package nextmethod.base;

import java.util.function.Supplier;

public final class SystemHelpers {

	static {
		NLSupplier = System::lineSeparator;
	}

	private SystemHelpers() {}

	public static Supplier<String> NLSupplier;

	public static String newLine() {
		return NLSupplier.get();
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
