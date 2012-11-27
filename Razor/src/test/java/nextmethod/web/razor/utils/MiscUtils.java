package nextmethod.web.razor.utils;

import nextmethod.base.Debug;
import nextmethod.base.Delegates;
import nextmethod.web.razor.DebugArgs;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertEquals;

public final class MiscUtils {

	private MiscUtils() {}

	public static final int TimeoutInSeconds = 1;

	public static void DoWithTimeoutIfNotDebugging(@Nonnull Delegates.IFunc1<Long, Boolean> withTimeout) {
		if (Debug.isDebugArgPresent(DebugArgs.DebuggerIsAttached)) {
			withTimeout.invoke(Long.MAX_VALUE);
		}
		else {
			assertEquals("Timeout Expired!", true, withTimeout.invoke(TimeUnit.SECONDS.toMillis(TimeoutInSeconds)).booleanValue());
		}
	}
}
