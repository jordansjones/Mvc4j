package nextmethod.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

/**
 *
 */
public final class Debug {

	private static final Logger logger = LoggerFactory.getLogger(Debug.class);

	public static void doAssert(final boolean expression, final String message) {
		if (isAssertEnabled && !expression && logger.isDebugEnabled()) {
			logger.debug(message);
		}
	}

	public static boolean isDebugArgPresent(final String argName) {
		return Boolean.getBoolean(argName);
	}

	/**
	 * Emits the specified error message
	 * @param message    Error message to emit
	 */
	public static void fail(@Nonnull final String message) {
		if (isAssertEnabled) {
			logger.error("---- DEBUG ASSERTION FAILED ----" + System.lineSeparator() + message);
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
