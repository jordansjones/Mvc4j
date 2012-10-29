package nextmethod.web.razor.editor.internal;

import com.google.common.base.Optional;
import nextmethod.annotations.Internal;
import nextmethod.base.NotImplementedException;

// TODO
@Internal
public final class RazorEditorTrace {

	private RazorEditorTrace() {}

	private static Optional<Boolean> enabled = Optional.absent();

	private static boolean isEnabled() {
		throw new NotImplementedException();
	}

	public static void traceLine(final String format, final Object... args) {
		throw new NotImplementedException();
	}
}
