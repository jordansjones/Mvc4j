package nextmethod.web.razor.editor.internal;

import nextmethod.annotations.Internal;
import nextmethod.base.Debug;
import nextmethod.web.razor.DebugArgs;

import java.text.MessageFormat;

import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

// TODO: Use something better than System.out
@Internal
public final class RazorEditorTrace {

	private RazorEditorTrace() {}

	private static final Object lockObj = new Object();
	private static Boolean enabled;

	static {
		synchronized (lockObj) {
			if (enabled == null) {
				final boolean isEnabled;
				if (Debug.isDebugArgPresent(DebugArgs.RazorEditorTrace)) {
					isEnabled = true;
				}
				else {
					isEnabled = false;
				}
				System.out.println(
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
		if (isEnabled() && Debug.isDebugArgPresent(DebugArgs.EditorTracing)) {
			System.out.println(RazorResources().traceFormat(
				args != null && args.length > 0 ? MessageFormat.format(format, args) : format
			));
		}
	}
}
