package nextmethod.base;

import javax.annotation.Nonnull;

/**
 *
 */
public final class Debug {

	/**
	 * Emits the specified error message
	 * @param message    Error message to emit
	 */
	public static void fail(@Nonnull final String message) {
		if (isAssertEnabled) {
			System.err.println("---- DEBUG ASSERTION FAILED ----");
			System.err.println(message);
		}
	}

	private static final boolean isAssertEnabled = checkIfAssertIsEnabled();

	public static boolean isAssertEnabled() {
		return isAssertEnabled;
	}

	@SuppressWarnings({"ConstantConditions", "AssertWithSideEffects"})
	private static boolean checkIfAssertIsEnabled() {
		boolean isEnabled = false;
		assert isEnabled = true;
		return isEnabled;
	}
}
