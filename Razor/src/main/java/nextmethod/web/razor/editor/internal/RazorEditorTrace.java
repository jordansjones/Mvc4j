package nextmethod.web.razor.editor.internal;

import nextmethod.annotations.Internal;
import nextmethod.base.Debug;
import nextmethod.web.razor.DebugArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;

import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

// TODO: Use something better than System.out
@Internal
public final class RazorEditorTrace {

	private static final Logger logger = LoggerFactory.getLogger(RazorEditorTrace.class);

	private RazorEditorTrace() {}

	private static final Object lockObj = new Object();
	private static Boolean enabled;

	static {
		synchronized (lockObj) {
			if (enabled == null) {
				final boolean isEnabled = Debug.isDebugArgPresent(DebugArgs.RazorEditorTrace);

				logger.trace(
					RazorResources().traceStartup(
						isEnabled ? RazorResources().traceEnabled() : RazorResources().traceDisabled()
					)
				);
				enabled = isEnabled;
			}
		}
	}

	private static boolean isEnabled() {
		return enabled;
	}

	public static void traceLine(final String format, final Object... args) {
		if (isEnabled() && Debug.isDebugArgPresent(DebugArgs.EditorTracing) && logger.isTraceEnabled()) {
			logger.trace(RazorResources().traceFormat(
				args != null && args.length > 0 ? MessageFormat.format(format, args) : format
			));
		}
	}
}
