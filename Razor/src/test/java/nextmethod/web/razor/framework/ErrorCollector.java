package nextmethod.web.razor.framework;

import nextmethod.base.Delegates;
import nextmethod.base.IDisposable;
import nextmethod.base.Strings;
import nextmethod.web.razor.utils.DisposableAction;

import static nextmethod.base.SystemHelpers.newLine;

/**
 *
 */
public class ErrorCollector {

	private final StringBuilder message = new StringBuilder();
	private int indent = 0;

	private boolean success;

	public ErrorCollector() {
		success = true;
	}

	public boolean isSuccess() {
		return success;
	}

	public String getMessage() {
		return message.toString();
	}

	public void addError(final String msg, final Object... args) {
		append("F", msg, args);
		success = false;
	}

	public void addMessage(final String msg, final Object... args) {
		append("P", msg, args);
	}

	public IDisposable indent() {
		indent++;
		return new DisposableAction(this::unindent);
	}

	public void unindent() {
		indent--;
	}

	private void append(final String prefix, final String msg, final Object[] args) {
		message
			.append(prefix)
			.append(":")
			.append(Strings.repeat("\t", indent))
			.append(String.format(msg, args))
			.append(newLine());
	}
}
